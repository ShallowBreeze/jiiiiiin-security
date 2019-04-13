package cn.jiiiiiin.security.core.properties;

import cn.jiiiiiin.security.core.dict.SecurityConstants;
import cn.jiiiiiin.security.core.social.weixin.connect.WeixinOAuth2Template;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.social.oauth2.OAuth2Parameters;

import javax.annotation.PostConstruct;

/**
 * @author jiiiiiin
 */
@Setter
@Getter
@NoArgsConstructor
@Slf4j
@ToString
public class BrowserProperties {

    /**
     * session管理配置项
     */
    private SessionProperties session = new SessionProperties();

    /**
     * 前端应用的baseUrl
     */
    private String fontUrl = "http://jiiiiiin-server-manager:9000/";

    /**
     * 身份认证（登录）页面
     * 当应用判断需要进行身份认证之后，如果访问的渠道是非“客户端”，那么就会渲染下面的这个页面 {@link HttpSecurity#formLogin()} {@link FormLoginConfigurer#loginPage(String)}
     */
    private String signInUrl = SecurityConstants.DEFAULT_SIGN_IN_PAGE_URL;

    /**
     * 社交登录，如果需要用户注册，跳转的页面
     */
    private String signUpUrl = SecurityConstants.DEFAULT_SIGN_UP_PAGE_URL;

    /**
     * 退出成功时跳转的html输出页面，如果是移动端渠道访问则返回json数据，不走该页面。
     */
    private String signOutUrl = SecurityConstants.DEFAULT_SIGN_OUT_PAGE_URL;

    /**
     * 成功登出之后重定向的页面
     */
    private String signOutSuccessUrl = this.signInUrl;

    /**
     * 权限校验失败时候显示的页面
     */
    private String errorPage = "/403.html";

    /**
     * '记住我'功能的有效时间，单位（秒），默认1小时
     */
    private int rememberMeSeconds = 3600;

    /**
     * 代理地址，用于访问第三方授权服务标识自身应用的服务器地址
     * 防止 {@link WeixinOAuth2Template#buildAuthenticateUrl(OAuth2Parameters)} 调用基类返回的其实是一个本地测试地址的情况
     */
    private String proxyUri = "http://jiiiiiin-server-manager:9090/";


}
