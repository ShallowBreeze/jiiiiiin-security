/**
 *
 */
package cn.jiiiiiin.security.app.server;

import lombok.val;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展和解析JWT的信息
 *
 * 将自定义的信息加入到token中返回给客户端
 *
 * @author zhailiang
 * @author jiiiiiin
 */
public class CustomJwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // 自定义token元信息
        final Map<String, Object> info = new HashMap<>();
        // TODO 测试
        info.put("company", "jiiiiiin");
        // 设置附加信息
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }

}
