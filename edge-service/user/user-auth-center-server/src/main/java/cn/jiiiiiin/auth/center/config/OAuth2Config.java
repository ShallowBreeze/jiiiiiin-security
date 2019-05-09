package cn.jiiiiiin.auth.center.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@AllArgsConstructor
public class OAuth2Config {

//    private final TokenStore tokenStore;
//
//    /**
//     * https://github.com/geektime-geekbang/oauth2lab/blob/135e35eb8809d21df50b1a6eabcc45f7ff44e393/lab05/oauth-server/src/main/java/io/spring2go/config/OAuth2AuthorizationServerConfig.java
//     * @return
//     */
//    @Bean
//    public DefaultTokenServices tokenServices() {
//        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        defaultTokenServices.setTokenStore(tokenStore);
//        defaultTokenServices.setSupportRefreshToken(true);
//        return defaultTokenServices;
//    }
}
