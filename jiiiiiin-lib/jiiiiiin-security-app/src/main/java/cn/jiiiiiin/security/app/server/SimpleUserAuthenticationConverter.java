package cn.jiiiiiin.security.app.server;

import cn.jiiiiiin.module.common.dto.mngauth.AdminDto;
import cn.jiiiiiin.module.common.entity.mngauth.Admin;
import cn.jiiiiiin.module.mngauth.component.MngUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Collection;
import java.util.Map;

import static cn.jiiiiiin.security.app.server.CustomJwtTokenEnhancer.CACHE_PRINCIPAL;

@Slf4j
@AllArgsConstructor
public class SimpleUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    private final RedisTemplate redisTemplate;

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(CACHE_PRINCIPAL)) {
            AdminDto adminDto = (AdminDto) redisTemplate.opsForValue().get(map.get(CACHE_PRINCIPAL));
            MngUserDetails mngUserDetails = new MngUserDetails(adminDto);
            final Collection<? extends GrantedAuthority> authorities = MngUserDetails.getGrantedAuthority(adminDto);
            return new UsernamePasswordAuthenticationToken(mngUserDetails, "N/A", authorities);
        } else {
            return super.extractAuthentication(map);
        }
    }
}
