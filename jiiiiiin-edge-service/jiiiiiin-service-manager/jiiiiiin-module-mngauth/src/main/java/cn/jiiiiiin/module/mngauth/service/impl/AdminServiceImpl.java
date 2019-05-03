package cn.jiiiiiin.module.mngauth.service.impl;

import cn.jiiiiiin.module.common.dto.mngauth.AdminDto;
import cn.jiiiiiin.module.common.entity.mngauth.Admin;
import cn.jiiiiiin.module.common.entity.mngauth.Resource;
import cn.jiiiiiin.module.common.entity.mngauth.Role;
import cn.jiiiiiin.module.common.enums.common.ChannelEnum;
import cn.jiiiiiin.module.common.enums.common.StatusEnum;
import cn.jiiiiiin.module.common.exception.BusinessErrException;
import cn.jiiiiiin.module.common.mapper.mngauth.AdminMapper;
import cn.jiiiiiin.module.common.mapper.mngauth.InterfaceMapper;
import cn.jiiiiiin.module.common.mapper.mngauth.ResourceMapper;
import cn.jiiiiiin.module.mngauth.service.IAdminService;
import cn.jiiiiiin.security.core.social.controller.SocialCommSingUpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Service
@Slf4j
@AllArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    private final AdminMapper adminMapper;

    private final ResourceMapper resourceMapper;

    private final InterfaceMapper interfaceMapper;

    private final PasswordEncoder passwordEncoder;

    /**
     * browser 模块下使用
     */
    private final ProviderSignInUtils providerSignInUtils;

    /**
     * app 模块下使用
     */
    private final SocialCommSingUpUtils socialCommSingUpUtils;

    @Override
    public Admin signInByUsername(@NonNull String username, ChannelEnum channel) {
        val res = adminMapper.selectByUsername(username, channel);
        return handlerSignUser(username, channel, res);
    }

    private Admin handlerSignUser(@NonNull String username, ChannelEnum channel, Admin res) {
        if (res == null) {
            throw new UsernameNotFoundException(String.format("找不到%s对应的登录用户信息", username));
        }
        if (res.getRoles().stream().anyMatch(p -> p.getId().equals(Role.ROLE_ADMIN_ID))) {
            // 系统管理员拥有所有访问控制权限和菜单资源
            val adminRole = res.getRoles().stream().filter(p -> p.getId().equals(Role.ROLE_ADMIN_ID)).findFirst().get();
            val resources = resourceMapper.selectList(new QueryWrapper<Resource>().eq(Resource.CHANNEL, channel).eq(Resource.STATUS, StatusEnum.ENABLE).orderByAsc(Resource.LEVELS, Resource.NUM));
            // 查询资源关联的接口
            resources.forEach(resource -> resource.setInterfaces(interfaceMapper.selectByResourceId(resource.getId())));
            adminRole.setResources(resources);
        } else {
            res.getRoles().forEach(role -> {
                val resources = resourceMapper.selectByRoleId(role.getId(), channel);
                // 查询资源关联的接口
                resources.forEach(resource -> resource.setInterfaces(interfaceMapper.selectByResourceId(resource.getId())));
                role.setResources(resources);
            });
        }
        return res;
    }

    @Override
    public Admin signInByUsernameOrPhoneNumb(String username, ChannelEnum channel) {
        val res = adminMapper.signInByUsernameOrPhoneNumb(username, channel);
        return handlerSignUser(username, channel, res);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveRelationRoleRecords(Admin admin) {
        adminMapper.deleteRelationRoleAdminRecord(admin.getId());
        return SqlHelper.retBool(adminMapper.insertRelationRoleRecords(admin));
    }

    private void _checkAdminUniqueness(Admin admin) {
        val temp = adminMapper.selectByUsername(admin.getUsername(), admin.getChannel());
        if (temp != null) {
            throw new BusinessErrException(String.format("当前用户名【%s】已经存在，请重新选择一个用户名", admin.getUsername()));
        }
    }

    @Transactional
    @Override
    public Boolean saveAdminAndRelationRecords(AdminDto admin) {
        _checkAdminUniqueness(admin);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreateTime(LocalDateTime.now());
        val res = SqlHelper.retBool(adminMapper.insert(admin));
        _parseRoleIds2Roles(admin);
        saveRelationRoleRecords(admin);
        return res;
    }

    /**
     * 前端将添加和修改的用户管理角色ids存储在 中，
     * 需要进行转换以便{@link AdminServiceImpl#saveRelationRoleRecords(Admin)}可以完成管理记录
     *
     * @param admin
     */
    private void _parseRoleIds2Roles(@NonNull AdminDto admin) {
        // 因为更新的时候前端可能会将`旧的` admin#roles属性传回来，故这里指认roleIds属性
        val roles = new HashSet<Role>(admin.getRoleIds().length);
        for (String id : admin.getRoleIds()) {
            roles.add((Role) new Role().setId(Long.valueOf(id)));
        }
        admin.setRoles(roles);
    }

    @Transactional
    @Override
    public Boolean updateAdminAndRelationRecords(AdminDto admin) {
        val currentRecord = adminMapper.selectById(admin.getId());
        if (!currentRecord.getUsername().equals(admin.getUsername())) {
            _checkAdminUniqueness(admin);
        }
        currentRecord.setUsername(admin.getUsername());
        // 修改密码规则
        val pwd = admin.getPassword();
        if (StringUtils.isNotEmpty(pwd) && !passwordEncoder.matches(pwd, currentRecord.getPassword())) {
            currentRecord.setPassword(passwordEncoder.encode(pwd));
        }
        currentRecord.setPhone(admin.getPhone());
        currentRecord.setEmail(admin.getEmail());
        _parseRoleIds2Roles(admin);
        saveRelationRoleRecords(admin);
        return SqlHelper.retBool(adminMapper.updateById(currentRecord));
    }

    @Transactional
    @Override
    public Boolean removeAdminAndRelationRecord(Long id) {
        val currentRecord = adminMapper.selectById(id);
        adminMapper.deleteRelationRoleAdminRecord(id);
        return SqlHelper.delBool(adminMapper.deleteById(currentRecord));
    }

    @Override
    public Boolean removeAdminsAndRelationRecords(String idList) {
        val ids = Arrays.asList(idList.split(","));
        adminMapper.deleteRelationRoleAdminRecords(ids);
        return SqlHelper.delBool(adminMapper.deleteBatchIds(ids));
    }

    @Override
    public IPage<AdminDto> pageAdminDto(Page<AdminDto> page, ChannelEnum channel, AdminDto adminDto) {
        return adminMapper.selectPageAdminDto(page, channel, adminDto);
    }

    @Override
    public AdminDto getAdminAndRelationRecords(Long id) {
        val res = adminMapper.selectRoleRecordsById(id);
        return adminMapper.selectRoleRecordsById(id);
    }

    @Override
    public Boolean updatePwd(AdminDto admin) {
        val currentRecord = adminMapper.selectById(admin.getId());
        // 修改密码规则
        val pwd = admin.getPassword();
        if (StringUtils.isEmpty(pwd)) {
            throw new BusinessErrException("密码不能为空");
        }
        val length = pwd.length();
        if (length < 4 || length > 16) {
            throw new BusinessErrException("密码长度必须在（4~16）位之间");
        }
        if (passwordEncoder.matches(pwd, currentRecord.getPassword())) {
            throw new BusinessErrException("新旧密码相同");
        }
        currentRecord.setPassword(passwordEncoder.encode(pwd));
        return SqlHelper.retBool(adminMapper.updateById(currentRecord));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void regist(AdminDto admin, HttpServletRequest request) {
        saveAdminAndRelationRecords(admin);
        // 不管是注册用户还是绑定用户，都会拿到一个用户唯一标识。
        String userId = admin.getUsername();
        // 插入关联数据，针对social的UserConnection表
//        providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
        // 针对app
        socialCommSingUpUtils.doPostSignUp(new ServletWebRequest(request), userId);
    }
}
