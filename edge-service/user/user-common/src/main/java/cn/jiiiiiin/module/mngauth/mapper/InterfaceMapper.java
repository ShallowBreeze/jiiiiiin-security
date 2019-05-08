package cn.jiiiiiin.module.mngauth.mapper;

import cn.jiiiiiin.module.mngauth.entity.Interface;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * 系统接口表 Mapper 接口
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-12-09
 */
public interface InterfaceMapper extends BaseMapper<Interface> {

    /**
     * 通过资源id查询关联的接口集合
     *
     * @param resourceId
     * @return
     */
    Set<Interface> selectByResourceId(Long resourceId);

    /**
     * 删除接口管理记录
     *
     * @param idList
     * @return
     */
    Boolean deleteRelationRecords(@Param("idList") Collection<? extends Serializable> idList);
}
