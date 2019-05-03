package cn.jiiiiiin.security.app.component.authentication.social.impl;

import cn.jiiiiiin.security.core.social.support.SocialAuthenticationFilterPostProcessor;
import lombok.AllArgsConstructor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * https://coding.imooc.com/lesson/134.html#mid=7234
 *
 * @author jiiiiiin
 */
@Component
@AllArgsConstructor
public class AppSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    public void process(SocialAuthenticationFilter socialAuthenticationFilter) {
        // 修改spring social的默认授权后处理
        // 用于在社交登录之后下发访问令牌
        socialAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
    }
}
