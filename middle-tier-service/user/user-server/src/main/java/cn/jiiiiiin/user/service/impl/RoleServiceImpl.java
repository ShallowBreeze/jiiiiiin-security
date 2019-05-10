package cn.jiiiiiin.user.service.impl;

import cn.jiiiiiin.user.common.exception.BusinessErrException;
import cn.jiiiiiin.user.dto.RoleDto;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.entity.Role;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.mapper.ResourceMapper;
import cn.jiiiiiin.user.mapper.RoleMapper;
import cn.jiiiiiin.user.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public IPage<RoleDto> pageDto(Page<RoleDto> roleDtoPage, ChannelEnum channel, Role role) {
        return roleMapper.selectPageDto(roleDtoPage, channel, role);
    }

    @Transactional
    @Override
    public Boolean saveSelfAndRelationRecords(Role role) {
        Role.checkRootRole(role, "不能创建和系统管理员角色相同名称或相同标识的记录");
        _saveCheckRoleUniqueness(role);
        val res = SqlHelper.retBool(roleMapper.insert(role));
        _insertOrUpdateRelationResourceRecords(role);
        return res;
    }

    private void _saveCheckRoleUniqueness(Role role) {
        if (null != roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, role.getAuthorityName()))) {
            throw new BusinessErrException(String.format("系统已经存在角色标识为【%s】的记录", role.getAuthorityName()));
        }
        if (null != roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.NAME, role.getName()))) {
            throw new BusinessErrException(String.format("系统已经存在角色名字为【%s】的记录", role.getAuthorityName()));
        }
    }

    private void _updateCheckRoleUniqueness(Role role) {
        val currentRecord = roleMapper.selectById(role);
        if (currentRecord == null){
            throw new BusinessErrException(String.format("系统不存在角色标识为【%s】的记录", role.getAuthorityName()));
        }
        if (!currentRecord.getAuthorityName().equals(role.getAuthorityName())) {
            if (null != roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, role.getAuthorityName()))) {
                throw new BusinessErrException(String.format("系统已经存在角色标识为【%s】的记录", role.getAuthorityName()));
            }
        }
        if (!currentRecord.getName().equals(role.getName())) {
            if (null != roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.NAME, role.getName()))) {
                throw new BusinessErrException(String.format("系统已经存在角色名字为【%s】的记录", role.getAuthorityName()));
            }
        }

    }

    @Transactional
    @Override
    public Boolean updateSelfAndRelationRecords(Role role) {
        Role.checkRootRole(role, "系统管理员角色不允许修改");
        _updateCheckRoleUniqueness(role);
        val res = SqlHelper.retBool(roleMapper.updateById(role));
        _insertOrUpdateRelationResourceRecords(role);
        return res;
    }

    @Override
    public Boolean saveRelationResourceRecords(Role role) {
        return SqlHelper.retBool(roleMapper.insertRelationResourceRecords(role));
    }

    /**
     * 添加/更新角色资源记录关联表
     * @param role
     */
    private void _insertOrUpdateRelationResourceRecords(@NonNull Role role) {
        _clearRelationResourceRecords(role);
        // 过滤根节点
        role.setResources(role.getResources().stream().filter(p -> !p.getId().equals(Resource.IS_ROOT_MENU)).collect(Collectors.toList()));
        if (role.getResources().size() > 0) {
            roleMapper.insertRelationResourceRecords(role);
        }
    }

    private void _clearRelationResourceRecords(Role role) {
        val idList = new ArrayList<Long>();
        idList.add(role.getId());
        roleMapper.deleteRelationResourceRecords(idList);
    }

    @Override
    public RoleDto getRoleAndRelationRecords(Long id) {
        val role = roleMapper.selectRoleAndRelationRecords(id);
        // 设置前端element-ui tree展开属性数据
        val arr = new ArrayList<String>(role.getResources().size());
        role.getResources().forEach(item -> arr.add(String.valueOf(item.getId())));
        val temp = new String[arr.size()];
        val keys = arr.toArray(temp);
        role.setCheckedKeys(keys);
        role.setExpandedKeys(keys);
        return role;
    }

    @Transactional
    @Override
    public Boolean remove(Collection<? extends Serializable> idList) {
        // 检查是否为`系统管理员角色`
        Role.checkRootRole(idList, "系统管理员角色不允许删除");
        roleMapper.deleteRelationAdminRecords(idList);
        roleMapper.deleteRelationResourceRecords(idList);
        return SqlHelper.delBool(roleMapper.deleteBatchIds(idList));
    }

}
