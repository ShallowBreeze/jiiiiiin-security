package cn.jiiiiiin.user.mapper;

import cn.jiiiiiin.user.dto.AdminDto;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 查询用户即其具有的role角色集合
     *
     * @param username
     * @return
     */
    Admin selectByUsername(@Param("username") String username, @Param("channel") ChannelEnum channel);

    Admin signInByUsernameOrPhoneNumb(@Param("usernameOrPhoneNumb") String usernameOrPhoneNumb, @Param("channel") ChannelEnum channel);

    /**
     * 清空admin对应的role记录
     *
     * @param id
     * @return (> = 0 - 标识成功 ， 否则标识失败)
     */
    int deleteRelationRoleAdminRecord(Long id);

    /**
     * 批量插入admin关联的role记录
     * <p>
     * https://my.oschina.net/zjllovecode/blog/1818716
     *
     * @param admin
     * @return {@link com.baomidou.mybatisplus.extension.toolkit.SqlHelper#retBool(Integer)} 批量插入是否成功（0-失败，1-成功）
     */
    int insertRelationRoleRecords(Admin admin);

    int deleteRelationRoleAdminRecords(@Param("idList") Collection<? extends Serializable> idList);

    IPage<AdminDto> selectPageAdminDto(Page<AdminDto> page, @Param("channel") ChannelEnum channel, @Param("adminDto") AdminDto adminDto);

    AdminDto selectRoleRecordsById(Long id);

}
