package cn.jiiiiiin.auth.center.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 解决:https://stackoverflow.com/questions/17439628/spring-security-custom-exception-message-from-userdetailsservice描述的问题
 * @author jiiiiiin
 */
public class AuthCenterUsernameNotFoundException extends AuthenticationException {
    public AuthCenterUsernameNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthCenterUsernameNotFoundException(String msg) {
        super(msg);
    }
}
