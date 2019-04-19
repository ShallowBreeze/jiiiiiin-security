/**
 *
 */
package cn.jiiiiiin.security.core.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 认证服务器注册的第三方应用配置项
 *
 * 可以自行添加需要配置的属性{@link ClientDetailsServiceBuilder.ClientBuilder}
 *
 * @author zhailiang
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class OAuth2ClientProperties {

    /**
     * 第三方应用appId
     */
    private String clientId;
    /**
     * 第三方应用appSecret
     */
    private String clientSecret;
    /**
     * 针对此应用发出的token的有效时间
     */
    private int accessTokenValidateSeconds = 7200;

}
