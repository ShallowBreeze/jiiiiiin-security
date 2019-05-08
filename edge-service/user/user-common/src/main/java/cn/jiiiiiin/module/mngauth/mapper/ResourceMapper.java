package cn.jiiiiiin.module.mngauth.mapper;

import cn.jiiiiiin.module.mngauth.dto.ResourceDto;
import cn.jiiiiiin.module.mngauth.entity.Resource;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import cn.jiiiiiin.module.mngauth.enums.StatusEnum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限资源表 Mapper 接口
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 查询role关联的资源集合
     *
     * @param roleId
     * @return
     */
    List<Resource> selectByRoleId(@Param("roleId") Long roleId, @Param("channel") ChannelEnum channel);

    /**
     * 获取`pid`对应的下一级子节点
     *
     * @param pid
     * @param channel
     * @return
     */
    List<Resource> selectChildren(@Param("pid") Long pid, @Param("channel") ChannelEnum channel);

    /**
     * 获取`pid`其下的所有子节点
     *
     * @param pid
     * @param status
     * @return
     */
    List<Resource> selectAllChildrenNode(@Param("pid") Long pid, @Param("channel") ChannelEnum channel, @Param("status") StatusEnum status);

    /**
     * 查询给定资源下还存在多少一级子节点
     *
     * @param pid
     * @return
     */
    Integer selectCountChildren(@Param("pid") Long pid, @Param("channel") ChannelEnum channel);


    /**
     * 清理对应的资源和角色关联的记录
     * @param id
     */
    Boolean deleteRelationRoleRecords(Long id);

    /**
     * 插入关联的接口记录{@link ResourceDto}
     * @param resource
     * @return
     */
    Boolean insertRelationInterfaceRecords(ResourceDto resource);

    /**
     * 通过资源id查询自身和关联记录
     * @param id
     * @return
     */
    ResourceDto selectResourceAndRelationRecords(Long id);

    /**
     * 清理资源（id）所关联的接口记录
     * @param id
     * @return
     */
    Boolean deleteRelationInterfaceRecords(Long id);
}
