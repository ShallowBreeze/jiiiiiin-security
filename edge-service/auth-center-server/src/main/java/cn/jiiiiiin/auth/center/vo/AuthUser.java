package cn.jiiiiiin.auth.center.vo;

import cn.jiiiiiin.user.entity.Admin;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.security.Principal;
import java.util.Map;

/**
 * 认证对象+Token对象
 * 一般返回给前端用户基本信息+Token信息就行了
 *
 * @author jiiiiiin
 */
@Getter
@Setter
@Accessors(chain = true)
public class AuthUser {

    /**
     * 简单起见响应细节由`user-server`对应端点控制
     */
    private Admin admin;

    /**
     * 主要是{@link org.springframework.security.oauth2.provider.endpoint.TokenEndpoint#postAccessToken(Principal, Map)} 响应的token信息
     */
    private OAuth2AccessToken oAuth2AccessToken;

    private UserDetails userDetails;

    private OAuth2Authentication authentication;

}
