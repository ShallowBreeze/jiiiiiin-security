package cn.jiiiiiin.user.service;

import cn.jiiiiiin.user.dto.ResourceDto;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.enums.StatusEnum;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限资源表 服务类
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
public interface IResourceService extends IService<Resource> {

    /**
     * 保存资源并检查父节点下面的子节点的排序
     *
     * @param resource
     * @return
     */
    Boolean saveAndSortNumAndRelationInterfaceRecords(ResourceDto resource);

    /**
     * 注意：
     * 更新不能修改其父节点的关联关系
     * 不予许修改节点`type`
     * 不会修改子节点`children`属性
     *
     * @param resource
     * @return
     */
    Boolean updateAndSortNumAndRelationInterfaceRecords(ResourceDto resource);

    /**
     * 通过父节点id获取其下的所有资源
     * <p>
     * 注意：没有进行`treeAllChildrenNode`排序
     *
     * @param pid
     * @param channel
     * @return
     */
    List<Resource> treeAllChildrenNode(Long pid, ChannelEnum channel);

    /**
     * 通过父节点id查询其下的匹配资源
     * @param pid
     * @param channel
     * @param status
     * @return
     */
    List<Resource> searchTreeAllChildrenNode(Long pid, ChannelEnum channel, StatusEnum status);

    /**
     * 检测待删除的节点是否为叶子节点,并检查父节点下面的子节点的排序
     *
     * @param id
     * @param channel
     * @return
     */
    Boolean delOnlyIsLeafNode(Long id, ChannelEnum channel);

    /**
     * 通过资源id查询自身和关联记录
     * @param id
     * @return
     */
    ResourceDto getResourceAndRelationRecords(Long id);
}
