package cn.jiiiiiin.auth.center.component.oauth;

import cn.jiiiiiin.user.dto.CommonUserDetails;
import cn.jiiiiiin.user.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static cn.jiiiiiin.auth.center.component.oauth.RedisJwtTokenEnhancer.CACHE_PRINCIPAL;

/**
 * 提供给{@link JwtAccessTokenConverter}配置，以用来解析{@link RedisJwtTokenEnhancer}缓存的认证用户信息
 */
@Slf4j
@AllArgsConstructor
@Component
public class RedisUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    private final RedisTemplate redisTemplate;

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(CACHE_PRINCIPAL)) {
            Admin adminDto = (Admin) redisTemplate.opsForValue().get(map.get(CACHE_PRINCIPAL));
            CommonUserDetails mngUserDetails = new CommonUserDetails(adminDto);
            final Collection<? extends GrantedAuthority> authorities = CommonUserDetails.getGrantedAuthority(adminDto);
            return new UsernamePasswordAuthenticationToken(mngUserDetails, "N/A", authorities);
        } else {
            log.warn("开启[RedisUserAuthenticationConverter]但是没有使用redis缓存用户数据，还是走了DefaultUserAuthenticationConverter基类方法");
            // 这里会带来性能问题，故做了自定义，具体问题可以参考：https://juejin.im/post/5c9191785188252d7941f87c
            return super.extractAuthentication(map);
        }
    }
}
