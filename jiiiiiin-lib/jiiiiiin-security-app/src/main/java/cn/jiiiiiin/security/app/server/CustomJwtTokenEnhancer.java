/**
 *
 */
package cn.jiiiiiin.security.app.server;

import cn.jiiiiiin.module.common.entity.mngauth.Admin;
import cn.jiiiiiin.module.mngauth.component.MngUserDetails;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
@AllArgsConstructor
public class CustomJwtTokenEnhancer implements TokenEnhancer {

    /**
     * token增强存储认证服务器认证之后的{@link org.springframework.security.core.userdetails.UserDetails}的key
     */
    static final String CACHE_PRINCIPAL = "cache_principal";
    private static final String TOKENENHANCER = "TokenEnhancer-userDetails-";

    private final RedisTemplate redisTemplate;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // 自定义token元信息
        final Map<String, Object> info = new HashMap<>();
        val principal = authentication.getUserAuthentication().getPrincipal();
        if (principal instanceof MngUserDetails) {
            val userDetails = (MngUserDetails) principal;
            val key = generateTokenEnhancerUserDetailsKey(userDetails);
            redisTemplate.opsForValue().set(key, userDetails.getAdmin());
            info.put(CACHE_PRINCIPAL, key);
        }

        // 设置附加信息
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }

    private static String generateTokenEnhancerUserDetailsKey(MngUserDetails userDetails) {
        return TOKENENHANCER.concat(String.valueOf(userDetails.getAdmin().getId()));
    }

}
