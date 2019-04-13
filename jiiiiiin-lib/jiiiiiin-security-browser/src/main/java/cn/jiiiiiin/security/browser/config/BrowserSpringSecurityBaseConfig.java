package cn.jiiiiiin.security.browser.config;

import cn.jiiiiiin.security.core.authentication.FormAuthenticationConfig;
import cn.jiiiiiin.security.core.authorize.AuthorizeConfigManager;
import cn.jiiiiiin.security.core.config.component.SmsCodeAuthenticationSecurityConfig;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.security.core.social.SocialConfig;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeSecurityConfig;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * @author jiiiiiin
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class BrowserSpringSecurityBaseConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    private final UserDetailsService userDetailsService;

    private final SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;

    /**
     * @see SocialConfig#socialSecurityConfig() 注入social配置到ss
     */
    private final SpringSocialConfigurer socialSecurityConfig;

    private final InvalidSessionStrategy invalidSessionStrategy;

    private final SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    private final FormAuthenticationConfig formAuthenticationConfig;

    private final LogoutSuccessHandler logoutSuccessHandler;

    private final AccessDeniedHandler accessDeniedHandler;

    private final PersistentTokenRepository persistentTokenRepository;

    private final AuthorizeConfigManager authorizeConfigManager;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * https://docs.spring.io/spring-security/site/docs/4.2.7.RELEASE/reference/htmlsingle/
     * <p>
     * https://ws3.sinaimg.cn/large/006tNbRwgy1fuib0js6rhj31kw0ju0vk.jpg
     * <p>
     * 默认使用下面的方式进行用户身份认证
     * .and()
     * .httpBasic();
     * <p>
     * security 过滤器链关键环节：
     *
     * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
     * 这个类是用来做身份认证（登录认证）
     * @see org.springframework.security.web.access.ExceptionTranslationFilter
     * 用来处理FilterSecurityInterceptor认证过程中认证失败时候的流程控制
     * @see org.springframework.security.web.access.intercept.FilterSecurityInterceptor#invoke(FilterInvocation)
     * 中的InterceptorStatusToken token = super.beforeInvocation(fi); 会进行身份认证授权判断
     * @see org.springframework.social.security.SocialAuthenticationFilter
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

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
                // 开启session管理配置：
                .sessionManagement()
                // 设置session过期之后处理的接口
                // .invalidSessionUrl(INVALID_SESSION_URL)
                // 设置session过期之后处理策略
                .invalidSessionStrategy(invalidSessionStrategy)
                // 设置的最大会话存活数量
                // 如：设置单个用户session存在系统的数量，如果设置为1，那么同一个用户后面登录的会话就会把签名的会话踢掉
                .maximumSessions(securityProperties.getBrowser().getSession().getMaximumSessions())
                // 用来做session被“剔除”之后的记录
                .expiredSessionStrategy(sessionInformationExpiredStrategy)
                // 如果查过设置的最大会话存活数量，就不允许后续会话在进来
                // 如：来控制并发登录：后一个会话不能剔除前一个会话，即如果一个用户已经存在会话，后面的渠道就登录不上的需求
                .maxSessionsPreventsLogin(securityProperties.getBrowser().getSession().isMaxSessionsPreventsLogin())
                // 设置不能剔除上一个登录用户，当session数量等于上面配置的最大数量
                .and()
                .and()
                .logout()
                // 定义退出登录发送的接口名称，默认为`/logout`
                .logoutUrl(securityProperties.getBrowser().getSignOutUrl())
                // 退出登录之后的处理类，将会接收到退出登录的请求，可以在这里做响应的业务处理，配置之后`logoutSuccessUrl`配置会失效
                .logoutSuccessHandler(logoutSuccessHandler)
                // 退出登录之后重定向的页面
                // .logoutSuccessUrl("/signOut.html")
                // 配置退出登录之后，删除浏览器cookie中的对应字段，这里是删除会话id
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .rememberMe()
                // 配置记住用户的配置
                // 配置将需要记住用户的用户名通过一下的dao设置到数据库
                .tokenRepository(persistentTokenRepository)
                // 设置记住用户的时长
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                // 需要业务系统自己实现
                //.userDetailsService(userDetailsService)
                .and()
                // 临时关闭防护
                .csrf().disable()
                // iframe 设置，以便swagger-ui页面能嵌入前端显示
                // https://stackoverflow.com/questions/28647136/how-to-disable-x-frame-options-response-header-in-spring-security
                .headers().frameOptions().disable()
        ;

        // 对请求进行授权，这个方法下面的都是授权的配置
        authorizeConfigManager.config(http.authorizeRequests());
    }

    /**
     * https://www.jianshu.com/p/e8651cf91ec5
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
