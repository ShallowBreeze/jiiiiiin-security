package cn.jiiiiiin.user.config;

import cn.jiiiiiin.user.dict.AuthDict;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
public class MngAuthConfig {

    // TODO 待整理
    @Bean
    @ConditionalOnMissingBean(name = "adminGrantedAuthority")
    public SimpleGrantedAuthority adminGrantedAuthority() {
        return new SimpleGrantedAuthority(AuthDict.ROLE_ADMIN_AUTHORITY_FULL_NAME);
    }
}
