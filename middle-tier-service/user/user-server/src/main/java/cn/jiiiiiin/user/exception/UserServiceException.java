package cn.jiiiiiin.user.exception;

import cn.jiiiiiin.mvc.common.exception.BusinessErrException;

/**
 * @author jiiiiiin
 */
public class UserServiceException extends BusinessErrException {
    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, long code) {
        super(message, code);
    }
}
