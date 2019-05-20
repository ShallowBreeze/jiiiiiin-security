package cn.jiiiiiin.security.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author jiiiiiin
 */
@Configuration
@EnableWebSecurity
public class AppSpringSecurityBaseConfig extends WebSecurityConfigurerAdapter {

    /**
     *  这一步的配置是必不可少的，否则SpringBoot会自动配置一个AuthenticationManager,覆盖掉内存中的用户
     *  https://github.com/lexburner/oauth2-demo/blob/650bcc3700f217bd92b4217f1213ea0e4d33bb3d/client-credentials-springboot2/src/main/java/moe/cnkirito/security/oauth2/config/SecurityConfiguration.java
     * @return
     * @throws Exception
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
