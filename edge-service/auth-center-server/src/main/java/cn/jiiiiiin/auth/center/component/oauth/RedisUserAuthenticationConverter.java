package cn.jiiiiiin.auth.center.component.oauth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@Component
public class RedisUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    private final RedisTemplate redisTemplate;

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
//        if (map.containsKey(CACHE_PRINCIPAL)) {
//            AdminDto adminDto = (AdminDto) redisTemplate.opsForValue().get(map.get(CACHE_PRINCIPAL));
//            MngUserDetails mngUserDetails = new MngUserDetails(adminDto);
//            final Collection<? extends GrantedAuthority> authorities = MngUserDetails.getGrantedAuthority(adminDto);
//            return new UsernamePasswordAuthenticationToken(mngUserDetails, "N/A", authorities);
//        } else {
//            // 这里会带来性能问题，故做了自定义，具体问题可以参考：https://juejin.im/post/5c9191785188252d7941f87c
//            return super.extractAuthentication(map);
//        }
        throw new RuntimeException("待改造");
    }
}
