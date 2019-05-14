/**
 *
 */
package cn.jiiiiiin.auth.center.component.oauth;

import cn.jiiiiiin.security.core.properties.OAuth2ClientProperties;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.user.dto.CommonUserDetails;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.jiiiiiin.security.core.properties.OAuth2ClientProperties.DEFAULT_ACCESSTOKEN_VALIDATESECONDS;

/**
 * 扩展和解析JWT的信息，将授权之后的用户详情缓存到redis，已解决OAuth2授权头参数超过http Header长度要求的问题
 *
 * 提供给 {@link cn.jiiiiiin.security.app.server.AppAuthorizationServerConfig}进配置，通过组件名
 *
 * @author zhailiang
 * @author jiiiiiin
 */
@AllArgsConstructor
@Component("jwtTokenEnhancer")
public class RedisJwtTokenEnhancer implements TokenEnhancer {

    /**
     * token增强存储认证服务器认证之后的{@link org.springframework.security.core.userdetails.UserDetails}的key
     */
    public static final String CACHE_PRINCIPAL = "cache_principal";
    private static final String TOKENENHANCER = "TokenEnhancer-userDetails-";
    /**
     * 默认延迟清理附加时间（秒）
     */
    private static final int DEF_DELAY = 30;
    private static final OAuth2ClientProperties DEF_CLIENT_INFO = new OAuth2ClientProperties().setAccessTokenValidateSeconds(DEFAULT_ACCESSTOKEN_VALIDATESECONDS);
    private final RedisTemplate redisTemplate;
    private final SecurityProperties securityProperties;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // 自定义token元信息
        final Map<String, Object> info = new HashMap<>();
        val principal = authentication.getUserAuthentication().getPrincipal();
        if (principal instanceof CommonUserDetails) {
            val userDetails = (CommonUserDetails) principal;
            val key = generateTokenEnhancerUserDetailsKey(userDetails);
            val clientId = authentication.getOAuth2Request().getClientId();
            val accessTokenValiditySeconds = _parseAccessTokenValiditySeconds(clientId, securityProperties.getOauth2().getClients()) + DEF_DELAY;
            // 找到对应的client信息，以配置的token失效时间+延迟 来设置这里存储的过期时间
            redisTemplate.opsForValue().set(key, userDetails.getAdmin(), accessTokenValiditySeconds, TimeUnit.SECONDS);
            info.put(CACHE_PRINCIPAL, key);
        }

        // 设置附加信息
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }

    /**
     * 找到对应的client信息，以得到配置的token失效时间
     */
    private int _parseAccessTokenValiditySeconds(String clientId, List<OAuth2ClientProperties> clients) {
        // Optional::
        // https://www.jianshu.com/p/82ed16613072
        return clients.stream()
                .filter(clientInfo -> clientId.equalsIgnoreCase(clientInfo.getClientId()))
                .findFirst()
                .orElse(DEF_CLIENT_INFO).getAccessTokenValidateSeconds();
    }

    /**
     * 生成缓存key
     * @param userDetails 缓存数据
     * @return 返回 {@link RedisJwtTokenEnhancer#TOKENENHANCER} + 用户id的缓存key
     */
    private static String generateTokenEnhancerUserDetailsKey(CommonUserDetails userDetails) {
        return TOKENENHANCER.concat(String.valueOf(userDetails.getAdmin().getId()));
    }

}
