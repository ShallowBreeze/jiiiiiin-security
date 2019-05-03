package cn.jiiiiiin.security.browser.controller;

import cn.jiiiiiin.security.core.dict.SecurityConstants;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.security.core.social.SocialController;
import cn.jiiiiiin.security.core.social.support.SocialUserInfo;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jiiiiiin:
 * security:
 * browser:
 * loginPage = /demo-signIn.html
 *
 * @author jiiiiiin
 */
@RestController
@Slf4j
public class BrowserSecurityController {

    /**
     * 当框架认为需要进行身份认证，会将请求缓存到requestCache中，这里我们在登录完成之后，从这个对象中拿出框架帮我们缓存的上一个被拦截的请求
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final SecurityProperties securityProperties;

    private final LiteDeviceResolver liteDeviceResolver;


    @Autowired
    public BrowserSecurityController(SecurityProperties securityProperties, LiteDeviceResolver liteDeviceResolver) {
        this.securityProperties = securityProperties;
        this.liteDeviceResolver = liteDeviceResolver;
    }

    /**
     * 即当需要身份认证时候需要访问该接口，该接口负责根据渠道去渲染（身份认证（登录）页面）或返回json提示
     * <p>
     * 对应spring security的`.loginPage([])`配置
     * 将区分请求的渠道进行不同的响应
     *
     * @param request
     * @param response
     * @return
     * @ResponseStatus(code = HttpStatus.UNAUTHORIZED) 返回的状态码标识返回给非网页版客户端，标识需要进行用户授权
     */
    @RequestMapping(SecurityConstants.DEFAULT_UNAUTHENTICATED_URL)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public R<String> requireAuthentication(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        if (this.liteDeviceResolver.resolveDevice(request).isNormal()) {
            // 获取到上一个被拦截的请求(原始请求）或者说引发进行身份认证跳转的请求
            final SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                final String transTarget = savedRequest.getRedirectUrl();
                log.info("需要进行身份认证的请求是 {}", transTarget);
            }
            var msg = request.getParameter(WebAttributes.AUTHENTICATION_EXCEPTION);
            // 直接跳转到登录页面
            log.debug("跳转到身份认证页面 {} {}", securityProperties.getBrowser().getSignInUrl(), msg);
            if(StringUtils.isEmpty(msg)){
                msg = "访问的服务需要身份认证";
            }
            // `WebAttributes.AUTHENTICATION_EXCEPTION`详见身份认证失败处理器`bean::authenticationFailureHandler`
            val url = securityProperties.getBrowser().getSignInUrl()+ "?" + WebAttributes.AUTHENTICATION_EXCEPTION + "=" + msg;
            redirectStrategy.sendRedirect(request, response, url);
            return null;
        }else {
            return R.failed("访问的服务需要身份认证");
        }
    }

    /**
     * ss权限配置中`invalidSessionUrl`配置
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(SecurityConstants.DEFAULT_SESSION_INVALID_URL)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public R<String> requireAuthenticationOnInvalidSession(HttpServletRequest request, HttpServletResponse response, Model entity) throws IOException {
        // 获取到上一个被拦截的请求(原始请求）
        final SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            final String transTarget = savedRequest.getRedirectUrl();
            log.debug("session 失效，需要进行身份认证的请求是 {}", transTarget);
        }
        if (this.liteDeviceResolver.resolveDevice(request).isNormal()) {
            // 直接跳转到登录页面
            log.debug("跳转到身份认证页面 {}", securityProperties.getBrowser().getSignInUrl());
            entity.addAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, "登录会话失效，访问的服务需要重新身份认证");
            redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getSignInUrl());
            return null;
        } else {
            return R.failed("登录会话失效，访问的服务需要重新身份认证");
        }
    }
}
