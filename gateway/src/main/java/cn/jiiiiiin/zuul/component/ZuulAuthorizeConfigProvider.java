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

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        config
                // TODO 待整合Swagger2到网关
                // .antMatchers(Swagger2Config.AUTH_WHITELIST).permitAll()
//                .anyRequest()
//                // 自定义权限表达式
//                .access("@rbacService.hasPermission(request, authentication)");
                .anyRequest()
                .authenticated();
        return true;
    }

}
