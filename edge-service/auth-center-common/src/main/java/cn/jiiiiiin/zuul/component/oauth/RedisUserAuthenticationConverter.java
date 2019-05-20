package cn.jiiiiiin.zuul.component.oauth;

import cn.jiiiiiin.mvc.common.utils.SpringMVC;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.vo.CommonUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 提供给{@link JwtAccessTokenConverter}配置，以用来解析认证中心`RedisJwtTokenEnhancer`缓存的认证用户信息
 *
 * @author jiiiiiin
 */
@Slf4j
@AllArgsConstructor
@Component
public class RedisUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    /**
     * 注意：这里的key的起始是认证中心`RedisJwtTokenEnhancer`
     */
    public static final String CACHE_PRINCIPAL = "cache_principal";

    private final RedisTemplate redisTemplate;

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        log.debug("extractAuthentication map: {}", map);
        val req = SpringMVC.getRequest();
        if (req != null) {
            val key = SpringMVC.getRequest().getHeader(CACHE_PRINCIPAL);
            if (StringUtils.isNotEmpty(key)) {
                Admin adminDto = (Admin) redisTemplate.opsForValue().get(key);
                CommonUserDetails mngUserDetails = new CommonUserDetails(adminDto);
                final Collection<? extends GrantedAuthority> authorities = CommonUserDetails.getGrantedAuthority(adminDto);
                log.debug("通过redis cache获取认证后的用户记录 {}", mngUserDetails);
                return new UsernamePasswordAuthenticationToken(mngUserDetails, "N/A", authorities);
            }
        }
        throw new OAuth2Exception("用户未进行身份认证，请重新登录之后再进行操作");
//        log.warn("开启[RedisUserAuthenticationConverter]但是没有使用redis缓存用户数据，还是走了DefaultUserAuthenticationConverter基类方法");
        // 这里会带来性能问题，故做了自定义，具体问题可以参考：https://juejin.im/post/5c9191785188252d7941f87c
//        return super.extractAuthentication(map);
    }


}