package cn.jiiiiiin.module.mngauth.service;

import cn.jiiiiiin.module.mngauth.dto.AdminDto;
import cn.jiiiiiin.module.mngauth.entity.Admin;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
public interface IAdminService extends IService<Admin> {

    /**
     * 除了下面角色，将会load对应用户具有角色的资源信息集合
     * {@link cn.jiiiiiin.module.mngauth.dict.AuthDict#ROLE_ADMIN_AUTHORITY_NAME}
     * <p>
     * 且区分不同的渠道
     *
     * @param username
     * @param channel
     * @return
     */
    Admin signInByUsername(@NonNull String username, ChannelEnum channel);

    /**
     * {@link IAdminService#signInByUsername(String, ChannelEnum)} 外加手机号检索
     * @param username
     * @param mng
     * @return
     */
    Admin signInByUsernameOrPhoneNumb(String username, ChannelEnum mng);

    /**
     * spring 事务：
     * https://www.ibm.com/developerworks/cn/java/j-master-spring-transactional-use/index.html
     * http://blog.didispace.com/springboottransactional/
     *
     * @param admin
     * @return
     */
    @Transactional
    boolean saveRelationRoleRecords(@NonNull Admin admin);

    /**
     * 保存用户和关联的角色记录
     *
     * @param admin
     * @return
     */
    Boolean saveAdminAndRelationRecords(AdminDto admin);

    /**
     * 更新用户和关联的角色记录
     *
     * @param admin
     * @return
     */
    Boolean updateAdminAndRelationRecords(AdminDto admin);

    /**
     * 删除和用户相关的记录和用户记录
     *
     * @param id
     * @return
     */
    Boolean removeAdminAndRelationRecord(Long id);

    /**
     * 批量删除用户和其相关记录
     *
     * @param idList
     * @return
     */
    Boolean removeAdminsAndRelationRecords(String idList);

    /**
     * 分页查询用户前端显示记录
     *
     * @param page
     * @param channel
     * @param adminDto
     * @return
     */
    IPage<AdminDto> pageAdminDto(Page<AdminDto> page, ChannelEnum channel, AdminDto adminDto);

    /**
     * 查询用户和管理记录
     *
     * @param id
     * @return
     */
    AdminDto getAdminAndRelationRecords(Long id);

    /**
     * 更新用户的密码
     * @param admin
     * @return
     */
    Boolean updatePwd(AdminDto admin);

    /**
     * 预注册基础用户信息
     * @param admin
     * @param request
     * @return
     */
    void regist(AdminDto admin, HttpServletRequest request);


}
