package cn.jiiiiiin.user.service.impl;

import cn.jiiiiiin.user.dto.ResourceDto;
import cn.jiiiiiin.user.entity.Interface;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.entity.Role;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.enums.StatusEnum;
import cn.jiiiiiin.user.common.exception.BusinessErrException;
import cn.jiiiiiin.user.mapper.ResourceMapper;
import cn.jiiiiiin.user.dict.AuthDict;
import cn.jiiiiiin.user.service.IResourceService;
import cn.jiiiiiin.user.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static cn.jiiiiiin.user.entity.Resource.IS_ROOT_MENU;

/**
 * <p>
 * 权限资源表 服务实现类
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private IRoleService roleService;

    private List<Resource> _parseTreeNode(Long pid, List<Resource> nodes) {
        val menus = new ArrayList<Resource>();
        nodes.forEach(resource -> {
            // 过滤一级节点
            if (resource.getPid().equals(pid)) {
                val node = Resource.parserMenu(resource, nodes);
                menus.add(node);
            }
        });
        menus.sort(Comparator.comparingInt(Resource::getNum));
        return menus;
    }

    @Override
    public List<Resource> treeAllChildrenNode(Long pid, ChannelEnum channel) {
        val nodes = resourceMapper.selectAllChildrenNode(pid, channel, null);
        return _parseTreeNode(pid, nodes);
    }

    @Override
    public List<Resource> searchTreeAllChildrenNode(Long pid, ChannelEnum channel, StatusEnum status) {
        val nodes = resourceMapper.selectAllChildrenNode(pid, channel, status);
        return _parseTreeNode(pid, nodes);
    }

    @Transactional
    @Override
    public Boolean saveAndSortNumAndRelationInterfaceRecords(ResourceDto resource) {
        // 检测是否存在重复的`name`|`alias`记录
        val existRecord = this.getOne(new QueryWrapper<Resource>().eq(Resource.CHANNEL, resource.getChannel()).eq(Resource.NAME, resource.getName()));
        if (existRecord != null) {
            throw new BusinessErrException(String.format("资源名称已经存在于当前渠道【%s】，请检查", resource.getChannel()));
        }
        if (!StringUtils.isBlank(resource.getAlias())) {
            // 检测是否存在重复的`alias`记录
            val existRecord2 = this.getOne(new QueryWrapper<Resource>().eq(Resource.CHANNEL, resource.getChannel()).eq(Resource.ALIAS, resource.getAlias()));
            if (existRecord2 != null) {
                throw new BusinessErrException(String.format("资源别名已经存在于当前渠道【%s】，请检查", resource.getChannel()));
            }
        }
        var res = false;
        val pid = resource.getPid();
        if (pid.equals(IS_ROOT_MENU)) {
            // 添加一级菜单
            resource.setPids(String.valueOf(IS_ROOT_MENU));
        } else {
            val pNode = resourceMapper.selectById(pid);
            // 设置新增资源的`pids`
            resource.setPids(pNode.getPids().concat(",").concat(String.valueOf(pid)));
        }
        // 检测排序
        final List<Resource> children = resourceMapper.selectChildren(pid, resource.getChannel());
        val size = children.size();
        val addNodeNum = resource.getNum();
        if (addNodeNum > size || size == 0) {
            res = SqlHelper.retBool(resourceMapper.insert(resource));
        } else {
            final boolean[] update = {false};
            // 需要排序
            children.forEach(item -> {
                val itemNum = item.getNum();
                if (itemNum.equals(addNodeNum)) {
                    update[0] = true;
                }
                if (update[0]) {
                    item.setNum(itemNum + 1);
                }
            });
            children.add(addNodeNum, resource);
            res = this.saveOrUpdateBatch(children);
        }

        if (res) {
            // 新增资源都默认加挂到`ROLE_ADMIN`
            val adminRole = roleService.getOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, AuthDict.ROLE_ADMIN_AUTHORITY_NAME));
            val resList = new ArrayList<Resource>();
            resList.add(resource);
            adminRole.setResources(resList);
            roleService.saveRelationResourceRecords(adminRole);

            // 插入管理`接口`记录
            val itfIds = resource.getInterfacesIds();
            if (itfIds != null && itfIds.length > 0) {
                resourceMapper.insertRelationInterfaceRecords(_parseInterfacesIds(resource));
            }
        }
        return res;
    }

    /**
     * 注意：使用{@link ResourceDto#interfacesIds}作为资源的关联接口记录，而不是使用{@link Resource#interfaces}
     *
     * @param resource
     * @return
     */
    private ResourceDto _parseInterfacesIds(@NonNull ResourceDto resource) {
        val itfIds = resource.getInterfacesIds();
        val newItf = new HashSet<Interface>();
        if (itfIds != null && itfIds.length > 0) {
            for (Long id : resource.getInterfacesIds()) {
                newItf.add((Interface) new Interface().setId(id));
            }
        }
        resource.setInterfaces(newItf);
        return resource;
    }

    @Transactional
    @Override
    public Boolean updateAndSortNumAndRelationInterfaceRecords(ResourceDto resource) {
        val currentNode = resourceMapper.selectById(resource.getId());
        if (currentNode == null) {
            throw new BusinessErrException(String.format("找不到待更新的资源记录【%s】", resource.getId()));
        }
        if (!resource.getName().equals(currentNode.getName())) {
            // 检测是否存在重复的`name`记录
            val existRecord = this.getOne(new QueryWrapper<Resource>().eq(Resource.CHANNEL, resource.getChannel()).eq(Resource.NAME, resource.getName()));
            if (existRecord != null) {
                throw new BusinessErrException(String.format("资源名称已经存在于当前渠道【%s】，请检查", resource.getChannel()));
            }
        }
        if (!StringUtils.isBlank(resource.getAlias()) && !resource.getAlias().equals(currentNode.getAlias())) {
            // 检测是否存在重复的`alias`记录
            val existRecord = this.getOne(new QueryWrapper<Resource>().eq(Resource.CHANNEL, resource.getChannel()).eq(Resource.ALIAS, resource.getAlias()));
            if (existRecord != null) {
                throw new BusinessErrException(String.format("资源别名已经存在于当前渠道【%s】，请检查", resource.getChannel()));
            }
        }

        Boolean res;
        val currentNum = currentNode.getNum();
        val modifyNum = resource.getNum();
        // 设置可更新属性
        currentNode
                .setNum(modifyNum)
                .setName(resource.getName())
                .setAlias(resource.getAlias())
                .setStatus(resource.getStatus())
                .setPath(resource.getPath())
                .setIcon(resource.getIcon());
        // 如果没有修改排序
        if (currentNum.equals(modifyNum)) {
            res = SqlHelper.retBool(resourceMapper.updateById(currentNode));
        } else {
            final List<Resource> children = resourceMapper.selectChildren(currentNode.getPid(), currentNode.getChannel());
            // 需要排序
            val childrenLength = children.size();
            var idx = -1;
            for (int i = 0; i < childrenLength; i++) {
                val item = children.get(i);
                if (!item.getId().equals(resource.getId())) {
                    val itemNum = item.getNum();
                    if (itemNum > currentNum && itemNum <= modifyNum) {
                        // 处理加大当前待修改节点的排序
                        item.setNum(itemNum - 1);
                    } else if (itemNum >= modifyNum && itemNum < currentNum) {
                        // 处理减小当前待修改节点的排序
                        item.setNum(itemNum + 1);
                    }
                } else {
                    idx = i;
                }
            }
            children.remove(idx);
            children.add(idx, currentNode);
            res = this.saveOrUpdateBatch(children);
        }

        if (res) {
            // 插入管理`接口`记录
            val itfIds = resource.getInterfacesIds();
            if (itfIds != null && itfIds.length > 0) {
                resourceMapper.deleteRelationInterfaceRecords(currentNode.getId());
                resourceMapper.insertRelationInterfaceRecords(_parseInterfacesIds(resource));
            }
        }
        return res;
    }

    @Transactional
    @Override
    public Boolean delOnlyIsLeafNode(Long id, ChannelEnum channel) {
        // 查询自身是否还存在子节点
        val numb = resourceMapper.selectCountChildren(id, channel);
        Boolean temp;
        if (numb > 0) {
            throw new IllegalStateException("待删除的资源还存在子节点，请先清空其下的子节点在进行改操作！");
        } else {
            val node = resourceMapper.selectById(id);
            final List<Resource> children = resourceMapper.selectChildren(node.getPid(), channel);
            if (node.getNum() == children.size()) {
                // 删除关联资源
                resourceMapper.deleteRelationRoleRecords(id);
                // 删除最后一个节点
                temp = SqlHelper.delBool(resourceMapper.deleteById(id));
            } else {
                final boolean[] update = {false};
                val size = children.size();
                var idx = -1;
                for (int i = 0; i < size; i++) {
                    val item = children.get(i);
                    if (item.getId().equals(id)) {
                        update[0] = true;
                        idx = i;
                        continue;
                    }
                    if (update[0]) {
                        item.setNum(item.getNum() - 1);
                    }
                }
                if (idx != -1) {
                    children.remove(idx);
                    if (!children.isEmpty()) {
                        this.saveOrUpdateBatch(children);
                    }
                }
                temp = SqlHelper.delBool(resourceMapper.deleteById(id));
            }
        }
        return temp;
    }

    @Override
    public ResourceDto getResourceAndRelationRecords(Long id) {
        return resourceMapper.selectResourceAndRelationRecords(id);
    }

}
