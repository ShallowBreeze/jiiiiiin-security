package cn.jiiiiiin.auth.center.component.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.Assert;

/**
 * @author jiiiiiin
 */
public class ThreadLocalOAuth2AuthenticationHolderStrategy {

    // ~ Static fields/initializers
    // =====================================================================================

    private static final ThreadLocal<OAuth2Authentication> contextHolder = new ThreadLocal<>();

    // ~ Methods
    // ========================================================================================================

    public void clearContext() {
        contextHolder.remove();
    }

    public OAuth2Authentication getContext() {
        OAuth2Authentication ctx = contextHolder.get();
        return ctx;
    }

    public void setContext(OAuth2Authentication context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }
}
