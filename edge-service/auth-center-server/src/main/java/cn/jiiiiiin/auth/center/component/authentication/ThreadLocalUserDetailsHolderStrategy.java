package cn.jiiiiiin.auth.center.component.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author jiiiiiin
 */
public class ThreadLocalUserDetailsHolderStrategy {

    // ~ Static fields/initializers
    // =====================================================================================

    private static final ThreadLocal<UserDetails> contextHolder = new ThreadLocal<>();

    // ~ Methods
    // ========================================================================================================

    public void clearContext() {
        contextHolder.remove();
    }

    public UserDetails getContext() {
        UserDetails ctx = contextHolder.get();
        return ctx;
    }

    public void setContext(UserDetails context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }
}
