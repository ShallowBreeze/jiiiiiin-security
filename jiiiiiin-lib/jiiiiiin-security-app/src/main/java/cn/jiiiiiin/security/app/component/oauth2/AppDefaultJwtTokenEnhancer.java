/**
 *
 */
package cn.jiiiiiin.security.app.component.oauth2;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * 扩展和解析JWT的信息
 *
 * 将自定义的信息加入到token中返回给客户端
 *
 * @author zhailiang
 * @author jiiiiiin
 */
@AllArgsConstructor
@Slf4j
public class AppDefaultJwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.debug("如果需要请配置自己的`TokenEnhancer`");
        return accessToken;
    }

}
