/**
 *
 */
package cn.jiiiiiin.component;

import cn.jiiiiiin.security.core.authorize.AuthorizeConfigProvider;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * @author zhailiang
 */
@Component
@Order(Integer.MAX_VALUE)
public class ZuulAuthorizeConfigProvider implements AuthorizeConfigProvider {

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        config
                .antMatchers(
                        "/js/**", "/css/**", "/img/**", "/images/**", "/fonts/**", "/**/favicon.ico",
                        // Druid监控的配置
                        "/",
                        "/index",
                        "/api/ac/oauth/*",
                        "/api/user/*"

//                        "/api/ac/testZuul"
//                        "/druid", "/druid/*", "/druid/**",
//                        // Spring Boot Admin监控配置
//                        "/actuator/**",
////                        "/oauth/**",
//                        // 用户注册需要放开
//                        "/admin/regist"
                ).permitAll()
                .anyRequest()
                .authenticated();
                // 自定义权限表达式
                //.access("@rbacService.hasPermission(request, authentication)");
        return true;
    }

}
