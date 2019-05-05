/**
 *
 */
package cn.jiiiiiin.security.core.validate.code;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;

/**
 * 校验码相关安全配置
 *
 * @author zhailiang
 */
@Component("validateCodeSecurityConfig")
@AllArgsConstructor
@ConditionalOnProperty(prefix = "jiiiiiin.security.oauth2", name = "enableAuthorizationServer", matchIfMissing = true)
public class ValidateCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    /**
     * @see ValidateCodeFilter
     */
    private final Filter validateCodeFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 添加自定义图形/短信验证码过滤器，校验session中的图形验证码
        http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }

}
