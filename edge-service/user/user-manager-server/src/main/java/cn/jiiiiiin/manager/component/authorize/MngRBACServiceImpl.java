/**
 *
 */
package cn.jiiiiiin.manager.component.authorize;

import cn.jiiiiiin.module.mngauth.entity.Interface;
import cn.jiiiiiin.module.mngauth.entity.Resource;
import cn.jiiiiiin.module.mngauth.entity.Role;
import cn.jiiiiiin.module.mngauth.component.MngUserDetails;
import cn.jiiiiiin.security.rbac.component.dict.RbacDict;
import cn.jiiiiiin.security.rbac.component.service.RBACService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhailiang
 */
@Slf4j
@Component("rbacService")
public class MngRBACServiceImpl implements RBACService {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${server.servlet.context-path:'/mng'}")
    private String replaceContextPath;

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
        val principal = authentication.getPrincipal();
        boolean hasPermission = false;
        if (principal instanceof MngUserDetails) {
            val admin = ((MngUserDetails) principal).getAdmin();
            val roles = admin.getRoles();
            val hasAdminRole = roles.stream().anyMatch(role -> role.getAuthorityName().equals(RbacDict.ROLE_ADMIN_AUTHORITY_NAME));
            if (hasAdminRole) {
                hasPermission = true;
            } else {
                // 前端`接口记录`只输入类似：'admin/search/*/*/*'，故这里要进行一个请求uri的replace修改

                var reqURI = request.getRequestURI();
                if(!"/".equals(replaceContextPath)){
                    // 如果配置了上下文需要将上下文过滤
                    reqURI = request.getRequestURI().replace(replaceContextPath, "");
                }
                val reqMethod = request.getMethod();
                // 读取用户所拥有权限的所有URL
                // 通过用户标识-》用户角色-》角色拥有的资源

                log.debug("内管权限校验开始：{} {} {}", admin.getUsername(), reqURI, reqMethod);
                for (Role role : roles) {
                    boolean temp;
                    for (Resource resource : role.getResources()) {
                        for (Interface anInterface : resource.getInterfaces()) {
                            temp = antPathMatcher.match(anInterface.getUrl(), reqURI) && reqMethod.equalsIgnoreCase(anInterface.getMethod());
                            if (temp) {
                                hasPermission = true;
                                break;
                            }
                        }
                    }
                }

                if (!hasPermission) {
                    log.debug("内管权限校验失败，权限不足 {} {} {} {}", admin.getUsername(), reqURI, reqMethod, roles);
                }
            }
        }

        return hasPermission;
    }

}
