/**
 *
 */
package cn.jiiiiiin.auth.center.component;

import cn.jiiiiiin.security.core.authorize.AuthorizeConfigProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * @author jiiiiiin
 */
@Component
public class AuthCenterAuthorizeConfigProvider implements AuthorizeConfigProvider {

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        // 注意：认证服务器不做权限校验，由zuul网关在前面卡住权限问题
        config.anyRequest().permitAll();
        return true;
    }

}
