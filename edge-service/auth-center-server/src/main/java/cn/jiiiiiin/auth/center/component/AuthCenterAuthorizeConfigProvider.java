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
        return false;
    }

}
