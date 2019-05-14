/**
 *
 */
package cn.jiiiiiin.security.core.social.controller;

import cn.jiiiiiin.security.core.dict.SecurityConstants;
import cn.jiiiiiin.security.core.social.SocialConfig;
import cn.jiiiiiin.security.core.social.SocialController;
import cn.jiiiiiin.security.core.social.support.SocialUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author zhailiang
 * @author jiiiiiin
 */
@RestController
@AllArgsConstructor
@ConditionalOnBean(name = "dataSource")
public class SocialSecurityController extends SocialController {

    private final ProviderSignInUtils providerSignInUtils;

    private final SocialCommSingUpUtils socialCommSingUpUtils;

    // 使用`AppSecurityController`替代当前接口
//    /**
//     * 用户第一次社交登录时，会引导用户进行用户注册或绑定，此服务用于在注册或绑定页面获取社交网站用户信息
//     * 端点：`"/social/userInfo"`
//     * @param request
//     * @return
//     */
//    @GetMapping(SecurityConstants.DEFAULT_SOCIAL_USER_INFO_URL)
//    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
//        // 从session中获取连接对象，在获取用户信息
//        // @see Connection
//        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
//        return buildSocialUserInfo(connection);
//    }


    /**
     * 需要注册时跳到这里，返回401（需要进行用户认证）和用户信息给前端
     * <p>
     * 客户端在收到授权用户信息之后，需要回显一个客户端的注册或绑定页面，回显授权用户信息
     *
     * @param request
     * @return 第三方授权用户信息
     * @see SocialCommSingUpUtils
     * @see SocialConfig#socialSecurityConfig()
     */
    @GetMapping(SecurityConstants.DEFAULT_SOCIAL_USER_INFO_URL)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
        // 在当前请求[第三方]通过重定向回到应用的时候，用户信息可以放到session，但下一次请求会新建一个session（token模式）
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        // 故需要进行缓存
        socialCommSingUpUtils.saveConnectionData(new ServletWebRequest(request), connection.createData());
        return buildSocialUserInfo(connection);
    }

}
