/**
 *
 */
package cn.jiiiiiin.security.core.config.component;

import cn.jiiiiiin.security.core.authentication.mobile.SmsCodeAuthenticationFilter;
import cn.jiiiiiin.security.core.authentication.mobile.SmsCodeAuthenticationProvider;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 短信登录配置
 * <p>
 * 将短信验证码过滤器添加到ss框架的过滤器链中
 * <p>
 * 需要将当前组件添加到对应的ss框架的认证配置中，相当于在自定义配置中添加当前的针对短信验证码的公共配置
 *
 * @author zhailiang
 * @author jiiiiiin
 */
@Component
@ConditionalOnProperty(prefix = "jiiiiiin.security.oauth2", name = "enableAuthorizationServer", havingValue = "true", matchIfMissing = true)
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final UserDetailsService userDetailsService;

    @Autowired(required = false)
    private PersistentTokenRepository persistentTokenRepository;

    public SmsCodeAuthenticationSecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler, UserDetailsService userDetailsService) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        val smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        // 注入认证管理器
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        // 注入认证成功处理器
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        // 注入认证失败处理器
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        if (persistentTokenRepository != null) {
            String key = UUID.randomUUID().toString();
            smsCodeAuthenticationFilter.setRememberMeServices(new PersistentTokenBasedRememberMeServices(key, userDetailsService, persistentTokenRepository));
        }
        // 配置支持认证的provider
        val smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        // 配置provider所需的user details service
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);
        http
                // 将自定义provider添加到authentication manager托管集合中
                .authenticationProvider(smsCodeAuthenticationProvider)
                // 将自定义authentication filter设置到sp过滤器链
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
