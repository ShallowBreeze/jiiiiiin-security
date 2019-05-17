package cn.jiiiiiin.auth.center.component.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * 用于在认证过程中（TOKEN）缓存{@link org.springframework.security.core.userdetails.UserDetailsService}获取到的`UserDetails`
 *
 * @author jiiiiiin
 */
public class OAuth2AuthenticationHolder {

    private static class Holder {
        // http://www.importnew.com/18872.html
        private static ThreadLocalOAuth2AuthenticationHolderStrategy singleton = new ThreadLocalOAuth2AuthenticationHolderStrategy();
    }

    public static ThreadLocalOAuth2AuthenticationHolderStrategy getSingleton() {
        return Holder.singleton;
    }

    public static OAuth2Authentication getContext() {
        return getSingleton().getContext();
    }

    public static void setContext(OAuth2Authentication context) {
        getSingleton().setContext(context);
    }

    public static void clearContext() {
        getSingleton().clearContext();
    }

}
