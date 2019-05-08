package cn.jiiiiiin.security.browser.component.authentication;

import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * [自定义AuthenticationEntryPoint](https://segmentfault.com/a/1190000012137647)
 * http://www.iocoder.cn/Spring-Security/laoxu/SpringSecurityFilterChain/
 *
 * {@link org.springframework.security.web.access.ExceptionTranslationFilter}
 *
 * @author jiiiiiin
 */
public class BrowserLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    protected LiteDeviceResolver liteDeviceResolver;

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public BrowserLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        val currentDevice = liteDeviceResolver.resolveDevice(request);
        // 根据渠道返回不同的响应数据
        if (!currentDevice.isNormal()) {
            // TODO 待测试
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        } else {
            super.commence(request, response, authException);
        }
    }

    @Override
    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        var res = super.buildRedirectUrlToLoginPage(request, response, authException);
        res += "?" + WebAttributes.AUTHENTICATION_EXCEPTION + "=" + authException.getMessage();
        return res;
    }
}
