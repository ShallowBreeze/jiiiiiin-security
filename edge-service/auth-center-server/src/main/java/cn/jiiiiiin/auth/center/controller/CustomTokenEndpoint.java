package cn.jiiiiiin.auth.center.controller;

import cn.jiiiiiin.auth.center.component.authentication.OAuth2AuthenticationHolder;
import cn.jiiiiiin.auth.center.component.authentication.UserDetailsHolder;
import cn.jiiiiiin.auth.center.exception.AuthCenterException;
import cn.jiiiiiin.auth.center.vo.AuthUser;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.vo.CommonUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    private final ConsumerTokenServices tokenServices;

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public AuthUser postAccessToken(Principal principal, @RequestParam
            Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        log.debug("自定义CustomTokenEndpoint#postAccessToken被执行");
        // 注意顺序不能变，必须先执行`postAccessToken`其内部会走认证流程，之后才能从上下文`UserDetailsHolder.getContext()`获取用户
        val oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return new AuthUser()
                .setAdmin(getUser())
                .setOAuth2AccessToken(oAuth2AccessToken);
    }

    private Admin getUser() {
        val userDetails = UserDetailsHolder.getContext();
        Admin admin;
        if (userDetails instanceof CommonUserDetails) {
            admin = ((CommonUserDetails) userDetails).getAdmin();
            admin.setPassword(null);
            return admin;
        } else {
            throw new AuthCenterException("暂不支持的UserDetails认证类型");
        }
    }

    private OAuth2Authentication getAuthentication() {
        val authentication = OAuth2AuthenticationHolder.getContext();
        if (authentication.getPrincipal() instanceof CommonUserDetails) {
            val rbacUserDetails = (CommonUserDetails) authentication.getPrincipal();
            // 注意，响应给前端的信息删除密码
            rbacUserDetails.getAdmin().setPassword(null);
        }
        return authentication;
    }

    /**
     * 目前使用jwt token是不支持或者说jwt规范不推荐这样做 {@link org.springframework.security.oauth2.provider.token.store.JwtTokenStore#removeAccessToken}
     * 那目前就只能是前端将对应的token info（access_token和refresh_token）清理
     * <p>
     * 使用 {@link ConsumerTokenServices#revokeToken(String tokenValue)} 方法，删除访问令牌。
     * https://www.baeldung.com/logout-spring-security-oauth
     * http://www.iocoder.cn/Spring-Security/OAuth2-learning/?vip
     * https://github.com/geektime-geekbang/oauth2lab/blob/master/lab05/oauth-server/src/main/java/io/spring2go/config/RevokeTokenEndpoint.java
     */
    @Deprecated
    @RequestMapping(value = "/clear/token", method = RequestMethod.DELETE)
    public void revokeToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("bearer")) {
            String tokenId = authorization.substring("bearer".length() + 1);
            tokenServices.revokeToken(tokenId);
        } else {
            throw new AuthCenterException("认证令牌参数传递错误，请检查");
        }
    }
}
