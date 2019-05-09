package cn.jiiiiiin.auth.center.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthCenterController {

    private final ConsumerTokenServices tokenServices;

    /**
     * 使用 {@link ConsumerTokenServices#revokeToken(String tokenValue)} 方法，删除访问令牌。
     * http://www.iocoder.cn/Spring-Security/OAuth2-learning/?vip
     * https://github.com/geektime-geekbang/oauth2lab/blob/master/lab05/oauth-server/src/main/java/io/spring2go/config/RevokeTokenEndpoint.java
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "access_token/revoke")
    public void revokeToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String tokenId = authorization.substring("Bearer".length() + 1);
            tokenServices.revokeToken(tokenId);
        }
    }

}
