/**
 *
 */
package cn.jiiiiiin.zuul.config.auth;

import cn.jiiiiiin.security.app.component.authentication.AppOAuth2WebSecurityExpressionHandler;
import cn.jiiiiiin.security.app.component.oauth2.AppWebResponseExceptionTranslator;
import cn.jiiiiiin.security.core.authorize.AuthorizeConfigManager;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeSecurityConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

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
public class ZuulResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final AuthorizeConfigManager authorizeConfigManager;

    private final AppOAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;

    /**
     * 负责令牌的存取
     */
    private final TokenStore tokenStore;

    private final AccessDeniedHandler accessDeniedHandler;
//
//    private final AccessDeniedHandler authenticationEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        val oAuth2AuthenticationProcessingFilter = _configOAuth2AuthenticationProcessingFilter();
        http
                // TODO need testing
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                //
//                // 配置`OAuth2AuthenticationProcessingFilter`鉴权被拒之后的自定义错误处理响应信息
//                .addFilterAfter(oAuth2AuthenticationProcessingFilter, AbstractPreAuthenticatedProcessingFilter.class)
                // 添加自定义验证码过滤器，校验session中的图形验证码
                .apply(validateCodeSecurityConfig)
                .and()
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
     * 自定义`OAuth2AuthenticationProcessingFilter`，主要是为了能自定义错误响应格式：
     * `oAuth2AuthenticationProcessingFilter.setAuthenticationEntryPoint(authenticationEntryPoint);`
     *
     * 注意：这样的做法相当于在系统默认的`OAuth2AuthenticationProcessingFilter`之前加了我们自定义的`OAuth2AuthenticationProcessingFilter`，他们做的核心工作是一样的，但是因为没有找到覆盖或者配置默认`OAuth2AuthenticationProcessingFilter`的地方，目前只能这样来做了
     */
    private OAuth2AuthenticationProcessingFilter _configOAuth2AuthenticationProcessingFilter() {
        val oAuth2AuthenticationProcessingFilter = new OAuth2AuthenticationProcessingFilter();

        val oauthAuthenticationManager = new OAuth2AuthenticationManager();
        val tokenService = new DefaultTokenServices();
        tokenService.setTokenStore(tokenStore);
        oauthAuthenticationManager.setTokenServices(tokenService);
        oAuth2AuthenticationProcessingFilter.setAuthenticationManager(oauthAuthenticationManager);

        val authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionTranslator(new AppWebResponseExceptionTranslator());
        oAuth2AuthenticationProcessingFilter.setAuthenticationEntryPoint(authenticationEntryPoint);

        return oAuth2AuthenticationProcessingFilter;
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