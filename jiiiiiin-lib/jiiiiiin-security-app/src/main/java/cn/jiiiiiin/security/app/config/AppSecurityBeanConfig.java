/**
 *
 */
package cn.jiiiiiin.security.app.config;

import cn.jiiiiiin.security.app.component.authentication.AppAuthenticationFailureHandler;
import cn.jiiiiiin.security.app.component.authentication.AppAuthenticationSuccessHandler;
import cn.jiiiiiin.security.app.component.authentication.AppOAuth2WebSecurityExpressionHandler;
import cn.jiiiiiin.security.app.component.authentication.social.openid.OpenIdAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.authentication.FormAuthenticationConfig;
import cn.jiiiiiin.security.core.authorize.AuthorizeConfigManager;
import cn.jiiiiiin.security.core.config.component.SmsCodeAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.social.SocialConfig;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeSecurityConfig;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 浏览器环境下扩展点配置，配置在这里的bean，业务系统都可以通过声明同类型或同名的bean来覆盖安全
 * 模块默认的配置。
 *
 * @author zhailiang
 * @author jiiiiiin
 */
@Configuration
public class AppSecurityBeanConfig {

    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AppAuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AppAuthenticationFailureHandler();
    }

    @EnableResourceServer
    @AllArgsConstructor
    @ConditionalOnMissingBean(ResourceServerConfigurerAdapter.class)
    public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {

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

}
