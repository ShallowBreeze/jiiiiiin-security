package cn.jiiiiiin.auth.center.exception;


/**
 * @author jiiiiiin
 */
public class AuthCenterException extends RuntimeException {

    public AuthCenterException() {
    }

    public AuthCenterException(String message) {
        super(message);
    }

    public AuthCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthCenterException(Throwable cause) {
        super(cause);
    }

    public AuthCenterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
