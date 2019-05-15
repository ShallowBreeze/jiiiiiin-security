/**
 *
 */
package cn.jiiiiiin.zuul.component;

import cn.jiiiiiin.security.core.authorize.AuthorizeConfigProvider;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * @author zhailiang
 */
@Component
@Order(Integer.MAX_VALUE)
@AllArgsConstructor
public class ZuulAuthorizeConfigProvider implements AuthorizeConfigProvider {

    private final SecurityProperties securityProperties;

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        securityProperties
                .getPublicApi()
                .forEach(url -> config.antMatchers(url).permitAll());
        config
//                .antMatchers(
//                        "/",
//                        "/index",
//                        "/api/ac/oauth/*"
////                        "/api/user/*"
////                        "/api/ac/testZuul"
//                        // Druid监控的配置
////                        "/druid", "/druid/*", "/druid/**",
////                        // Spring Boot Admin监控配置
////                        "/actuator/**",
//////                        "/oauth/**",
////                        // 用户注册需要放开
////                        "/admin/regist"
//                ).permitAll()
                .anyRequest()
                .authenticated();
                // TODO 添加自定义权限表达式
                //.access("@rbacService.hasPermission(request, authentication)");
        return true;
    }

}
