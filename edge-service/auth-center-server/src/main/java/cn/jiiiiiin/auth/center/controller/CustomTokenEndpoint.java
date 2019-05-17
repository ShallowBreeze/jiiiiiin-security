package cn.jiiiiiin.auth.center.controller;

import cn.jiiiiiin.auth.center.component.authentication.OAuth2AuthenticationHolder;
import cn.jiiiiiin.auth.center.component.authentication.RBACUserDetails;
import cn.jiiiiiin.auth.center.component.authentication.UserDetailsHolder;
import cn.jiiiiiin.auth.center.exception.AuthCenterException;
import cn.jiiiiiin.auth.center.vo.AuthUser;
import cn.jiiiiiin.user.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * https://stackoverflow.com/questions/31154557/customise-oath2-token-request-to-accept-extra-data/31183131#31183131
 *
 * @author jiiiiiin
 */
@RestController
@AllArgsConstructor
@Slf4j
public class CustomTokenEndpoint {

    private final TokenEndpoint tokenEndpoint;

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public AuthUser postAccessToken(Principal principal, @RequestParam
            Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        log.debug("自定义CustomTokenEndpoint#postAccessToken被执行");
        // 注意顺序不能变
        val oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
//        OAuth2Authentication authentication = getAuthentication();
        return new AuthUser()
                .setAdmin(getUser())
                .setOAuth2AccessToken(oAuth2AccessToken);
    }

    private Admin getUser() {
        val userDetails = UserDetailsHolder.getContext();
        Admin admin;
        if(userDetails instanceof RBACUserDetails){
            admin = ((RBACUserDetails) userDetails).getAdmin();
            admin.setPassword(null);
            return admin;
        } else {
            throw new AuthCenterException("暂不支持的UserDetails认证类型");
        }
    }

    private OAuth2Authentication getAuthentication() {
        val authentication = OAuth2AuthenticationHolder.getContext();
        if (authentication.getPrincipal() instanceof RBACUserDetails) {
            val rbacUserDetails = (RBACUserDetails) authentication.getPrincipal();
            // 注意，响应给前端的信息删除密码
            rbacUserDetails.getAdmin().setPassword(null);
        }
        return authentication;
    }
}
