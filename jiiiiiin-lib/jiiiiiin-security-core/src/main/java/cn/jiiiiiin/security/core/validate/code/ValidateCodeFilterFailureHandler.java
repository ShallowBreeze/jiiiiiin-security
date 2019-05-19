package cn.jiiiiiin.security.core.validate.code;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码校验失败回调，需要输出响应给调用方，类比认证失败处理器
 *
 * @author jiiiiiin
 */
public interface ValidateCodeFilterFailureHandler {
    void onValidateFailure(HttpServletRequest request, HttpServletResponse response, ValidateCodeException exception) throws IOException;
}
