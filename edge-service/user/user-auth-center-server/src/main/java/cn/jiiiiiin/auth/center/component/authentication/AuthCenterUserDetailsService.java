package cn.jiiiiiin.auth.center.component.authentication;

import cn.jiiiiiin.user.dto.AdminDto;
import cn.jiiiiiin.user.dto.Menu;
import cn.jiiiiiin.user.entity.Interface;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.component.MngUserDetails;
import cn.jiiiiiin.user.enums.ResourceTypeEnum;
import cn.jiiiiiin.user.service.IAdminService;
import cn.jiiiiiin.security.core.authentication.AuthenticationBeanConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * 内管`UserDetailsService`
 * <p>
 * 配置：{@link AuthenticationBeanConfig#userDetailsService()}
 *
 * @author jiiiiiin
 */
@Component
@Slf4j
public class AuthCenterUserDetailsService implements UserDetailsService, SocialUserDetailsService {

    /**
     * 注意：不要手动简化这里的注入方式
     */
    @Autowired
    private IAdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("普通登录用户名 {}", username);
        return _getUserDetails(username);
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        log.debug("社交登录用户名 {}", userId);
        return _getUserDetails(userId);
    }

    private SocialUserDetails _getUserDetails(String username) {
        // 根据channel去获取登录用户的权限信息
        val optionalAdmin = adminService.signInByUsernameOrPhoneNumb(username, ChannelEnum.MNG);
        if (optionalAdmin == null) {
            throw new UsernameNotFoundException("用户名密码不符");
        } else {
            val modelMapper = new ModelMapper();
            val adminDto = modelMapper.map(optionalAdmin, AdminDto.class);
            _parserResource(adminDto);
            return new MngUserDetails(adminDto);
        }
    }

    /**
     * 格式化前端需要的菜单
     *
     * @param optionalAdmin
     */
    private void _parserResource(AdminDto optionalAdmin) {
        val roles = optionalAdmin.getRoles();
        // 过滤菜单和授权资源
        val menuResources = new HashSet<Resource>();
        // 用户具有的资源
        val authorizeResources = new HashSet<Resource>();
        // 用户具有的接口集合
        val authorizeInterfaces = new HashSet<Interface>();
        roles.forEach(item -> item.getResources().forEach(resource -> {
            authorizeResources.add(resource);
            resource.getInterfaces().forEach(ife -> {
                authorizeInterfaces.add(ife);
            });
            if (resource.getType().equals(ResourceTypeEnum.MENU)) {
                menuResources.add(resource);
            }
        }));

        val menus = new ArrayList<Menu>();
        menuResources.forEach(resource -> {
            // 过滤一级节点
            if (resource.getPid().equals(Resource.IS_ROOT_MENU)) {
                val node = Menu.parserMenu(resource, menuResources);
                menus.add(node);
            }
        });
        menus.sort(Comparator.comparingInt(Menu::getNum));
        optionalAdmin.setAuthorizeResources(authorizeResources);
        optionalAdmin.setAuthorizeInterfaces(authorizeInterfaces);
        optionalAdmin.setMenus(menus);
        log.debug("响应的授权资源 {}", authorizeResources);
        log.debug("响应的授权接口集合 {}", authorizeInterfaces);
        log.debug("响应的菜单 {}", menus);
    }

}
