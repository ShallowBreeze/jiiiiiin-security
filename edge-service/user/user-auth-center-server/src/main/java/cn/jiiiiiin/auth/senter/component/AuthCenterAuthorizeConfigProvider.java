/**
 *
 */
package cn.jiiiiiin.auth.senter.component;

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
        config
//                .antMatchers(Swagger2Config.AUTH_WHITELIST).permitAll()
                .antMatchers(
                        "/js/**", "/css/**", "/img/**", "/images/**", "/fonts/**", "/**/favicon.ico",
                        // Druid监控的配置
                        "/",
                        "/index",
                        "/druid", "/druid/*", "/druid/**",
                        // Spring Boot Admin监控配置
                        "/actuator/**",
                        "/testZuul",
                        // 用户注册需要放开
                        "/admin/regist"
                ).permitAll();
        return false;
    }

}
