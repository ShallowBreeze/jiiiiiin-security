/**
 *
 */
package cn.jiiiiiin.auth.senter;

import cn.jiiiiiin.security.rbac.component.service.RBACService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhailiang
 */
@Component("rbacService")
public class TempRBACServiceImpl implements RBACService {

    /**
     * 需要读取用户所拥有权限的所有URL
     * 通过用户标识-》用户角色-》角色拥有的资源
     *
     * @param request        请求信息
     * @param authentication 身份认证信息
     * @return
     */
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        // principal即系统的UserDetailsService返回的用户标识对象，如果没有通过认证则是一个匿名的字符串
        return true;
    }

}
