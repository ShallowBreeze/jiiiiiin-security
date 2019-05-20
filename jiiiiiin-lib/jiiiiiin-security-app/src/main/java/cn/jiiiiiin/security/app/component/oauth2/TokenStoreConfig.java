/**
 *
 */
package cn.jiiiiiin.security.app.component.oauth2;

import cn.jiiiiiin.security.app.server.AppAuthorizationServerConfig;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 负责令牌的存取
 *
 * @author zhailiang
 * @author jiiiiiin
 * @see AppAuthorizationServerConfig
 */
@Configuration
public class TokenStoreConfig {

    /**
     * 透明令牌生成器
     *
     * 使用redis存储token的配置，只有在imooc.security.oauth2.tokenStore配置为redis时生效
     *
     * @author zhailiang
     */
    @Configuration
    @ConditionalOnProperty(prefix = "jiiiiiin.security.oauth2", name = "tokenStore", havingValue = "redis")
    @AllArgsConstructor
    public static class RedisConfig {

        /**
         * 链接工厂
         */
        private final RedisConnectionFactory redisConnectionFactory;

        @Bean
        public TokenStore redisTokenStore() {
            return new RedisTokenStore(redisConnectionFactory);
        }

    }

    /**
     * 使用jwt时的配置，默认生效
     *
     * @author zhailiang
     */
    @Configuration
    @ConditionalOnProperty(prefix = "jiiiiiin.security.oauth2", name = "tokenStore", havingValue = "jwt", matchIfMissing = true)
    @AllArgsConstructor
    public static class JwtConfig {

        private final SecurityProperties securityProperties;

        private final UserDetailsService userDetailsService;

        private final DefaultUserAuthenticationConverter defaultUserAuthenticationConverter;

        /**
         * @return
         * @see TokenStore 处理token的存储
         */
        @Bean
        public TokenStore jwtTokenStore() {
            return new JwtTokenStore(jwtAccessTokenConverter());
        }

        /**
         * @return
         * @see JwtAccessTokenConverter 处理token的生成
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            // https://coding.imooc.com/learn/questiondetail/113508.html
            // 解决`hasPermission`表达式如果`principal`是一个字符串问题
            final DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
            // 这里会带来性能问题，故做了自定义，具体问题可以参考：https://juejin.im/post/5c9191785188252d7941f87c
            // 建议重写`DefaultUserAuthenticationConverter`，目前解决方案需要在资源服务器配置`RedisUserAuthenticationConverter`
            defaultUserAuthenticationConverter.setUserDetailsService(userDetailsService);
            defaultAccessTokenConverter.setUserTokenConverter(defaultUserAuthenticationConverter);
            val converter = new JwtAccessTokenConverter();
            converter.setAccessTokenConverter(defaultAccessTokenConverter);
            // 指定密签秘钥
            converter.setSigningKey(securityProperties.getOauth2().getJwtSigningKey());
            return converter;
        }

    }


}
