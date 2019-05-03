/**
 *
 */
package cn.jiiiiiin.security.app.server;

import cn.jiiiiiin.security.app.component.authentication.AppOAuth2WebSecurityExpressionHandler;
import cn.jiiiiiin.security.app.component.authentication.social.openid.OpenIdAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.authentication.FormAuthenticationConfig;
import cn.jiiiiiin.security.core.authorize.AuthorizeConfigManager;
import cn.jiiiiiin.security.core.config.component.SmsCodeAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.security.core.social.SocialConfig;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeSecurityConfig;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.social.security.SpringSocialConfigurer;

import static cn.jiiiiiin.security.app.server.CustomAuthorizationServerConfig.SERVER_RESOURCE_ID;

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
public class CustomResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    private final OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;

    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;

    /**
     * @see SocialConfig#socialSecurityConfig() 注入social配置到ss
     */
    private final SpringSocialConfigurer socialSecurityConfig;

    private final FormAuthenticationConfig formAuthenticationConfig;

    private final AuthorizeConfigManager authorizeConfigManager;

    private final AppOAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        formAuthenticationConfig.configure(http);

        http
                // 添加自定义验证码过滤器，校验session中的图形验证码
                .apply(validateCodeSecurityConfig)
                .and()
                // 追加短信验证码公共配置
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()
                // 添加social拦截过滤器，引导用户进行社交登录,`SocialAuthenticationFilter`
                .apply(socialSecurityConfig)
                .and()
                // 添加针对`openid`第三方授权登录的token版本支持
                .apply(openIdAuthenticationSecurityConfig)
                .and()
                // 临时关闭防护
                .csrf().disable()
                // iframe 设置，以便swagger-ui页面能嵌入前端显示
                // https://stackoverflow.com/questions/28647136/how-to-disable-x-frame-options-response-header-in-spring-security
                .headers().frameOptions().disable();

        // 对请求进行授权，这个方法下面的都是授权的配置
        authorizeConfigManager.config(http.authorizeRequests());
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