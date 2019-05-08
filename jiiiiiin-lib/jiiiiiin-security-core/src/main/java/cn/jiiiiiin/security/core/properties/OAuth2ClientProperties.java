/**
 *
 */
package cn.jiiiiiin.security.core.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 认证服务器注册的第三方应用配置项
 *
 * @author zhailiang
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class OAuth2ClientProperties {

    public static final int DEFAULT_ACCESSTOKEN_VALIDATESECONDS = 7200;
    /**
     * 第三方应用appId
     */
    private String clientId;
    /**
     * 第三方应用appSecret
     */
    private String clientSecret;
    /**
     * 如果授权服务器支持`authorization_code`授权码模式，那么需要配置该属性，即当前客户应用接收授权码`code`的回调地址
     * 简化模式也需要配置
     */
    private String redirectUris;
    /**
     * 针对此应用发出的token的有效时间
     */
    private int accessTokenValidateSeconds = DEFAULT_ACCESSTOKEN_VALIDATESECONDS;

}
