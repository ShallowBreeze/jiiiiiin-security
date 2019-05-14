/**
 *
 */
package cn.jiiiiiin.auth;

import cn.jiiiiiin.security.app.component.authentication.AppOAuth2WebSecurityExpressionHandler;
import cn.jiiiiiin.security.app.component.authentication.social.openid.OpenIdAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.authentication.FormAuthenticationConfig;
import cn.jiiiiiin.security.core.authorize.AuthorizeConfigManager;
import cn.jiiiiiin.security.core.config.component.SmsCodeAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.social.SocialConfig;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeSecurityConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 资源服务器配置
 * <p>
 * 使用`@EnableResourceServer`注解标明当前应用是一个“资源服务器”提供商
 * <p>
 * 类`browser`项目针对spring security的权限配置类`BrowserSpringSecurityBaseConfig`
 *
 * @author zhailiang
 */
@Configuration
@EnableResourceServer
@AllArgsConstructor
@Slf4j
public class AuthResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final AuthorizeConfigManager authorizeConfigManager;

    private final AppOAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 临时关闭防护
                .csrf().disable()
                // iframe 设置，以便swagger-ui页面能嵌入前端显示
                // https://stackoverflow.com/questions/28647136/how-to-disable-x-frame-options-response-header-in-spring-security
                .headers().frameOptions().disable();

        // 对请求进行授权，这个方法下面的都是授权的配置
        authorizeConfigManager.config(http.authorizeRequests());
        log.debug("网关默认资源服务启动");
    }

    /**
     * 防止使用jwt token，spring security在权限认证的时候，使用自定义认证服务组件校验报错：
     *
     * `EL1057E: No bean resolver registered in the context to resolve access to bean 'permissionService'`
     *
     * 如：`.access("@permissionService.hasPermission(authentication,request)")`
     *
     * 参考：
     *
     * https://blog.csdn.net/liu_zhaoming/article/details/79411021
     *
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // 使用`.access("@rbacService.hasPermission(request, authentication)");`需要添加以下这行配置
        resources
//                .resourceId(SERVER_RESOURCE_ID)
                .expressionHandler(oAuth2WebSecurityExpressionHandler);
    }

}