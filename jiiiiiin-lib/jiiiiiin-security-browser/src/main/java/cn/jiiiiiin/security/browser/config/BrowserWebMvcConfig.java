package cn.jiiiiiin.security.browser.config;

import cn.jiiiiiin.security.core.dict.SecurityConstants;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jiiiiiin
 */
@Configuration
@AllArgsConstructor
public class BrowserWebMvcConfig implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if (securityProperties.getBrowser().getSignInUrl().equals(SecurityConstants.DEFAULT_SIGN_IN_PAGE_URL)) {
            // setViewName("signIn")指向`/jiiiiiin-security-browser/src/main/resources/templates/signIn.html`
            registry.addViewController(securityProperties.getBrowser().getSignInUrl()).setViewName(securityProperties.getBrowser().getSignInUrl().substring(1));
        }
        // 默认`social`会在oath授权成功之后访问该端点
        if (securityProperties.getBrowser().getSignUpUrl().equals(SecurityConstants.DEFAULT_SIGN_UP_PAGE_URL)) {
            registry.addViewController(securityProperties.getBrowser().getSignUpUrl()).setViewName(securityProperties.getBrowser().getSignUpUrl().substring(1));
        }
        if (securityProperties.getBrowser().getSession().getSessionInvalidUrl().equals(SecurityConstants.DEFAULT_SESSION_INVALID_URL)) {
            registry.addViewController(securityProperties.getBrowser().getSession().getSessionInvalidUrl()).setViewName(securityProperties.getBrowser().getSession().getSessionInvalidUrl().substring(1));
        }
        registry.addViewController("/connectStatus").setViewName("connectStatus");
        registry.addViewController("/userBinding").setViewName("userBinding");
    }

}
