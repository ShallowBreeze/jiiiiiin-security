package cn.jiiiiiin.auth.center.component.authentication;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用于在认证过程中（TOKEN）缓存{@link org.springframework.security.core.userdetails.UserDetailsService}获取到的`UserDetails`
 *
 * @author jiiiiiin
 */
public class UserDetailsHolder {

    private static class Holder {
        // http://www.importnew.com/18872.html
        private static ThreadLocalUserDetailsHolderStrategy singleton = new ThreadLocalUserDetailsHolderStrategy();
    }

    public static ThreadLocalUserDetailsHolderStrategy getSingleton() {
        return Holder.singleton;
    }

    public static UserDetails getContext() {
        return getSingleton().getContext();
    }

    public static void setContext(UserDetails context) {
        getSingleton().setContext(context);
    }

    public static void clearContext() {
        getSingleton().clearContext();
    }

}
