/**
 *
 */
package cn.jiiiiiin.security.app.server;

import cn.jiiiiiin.security.core.properties.SecurityProperties;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务器配置
 * <p>
 *     https://coding.imooc.com/lesson/134.html#mid=7236
 * <p>
 * ![](https://ws4.sinaimg.cn/large/0069RVTdgy1fuqnerfq5uj30w10g3q4o.jpg)
 * <p>
 * 上图也是下面的授权模式中，使用最多的授权码模式的交互流程；
 * <p>
 * ![](https://ws1.sinaimg.cn/large/0069RVTdgy1fuqn6o8vm2j30ox0bm3z0.jpg)
 *
 * <p>
 * https://oauth.net/2/
 * <p>
 * 4.1.  Authorization Code Grant
 * <p>
 * The authorization code grant type is used to obtain both access
 * tokens and refresh tokens and is optimized for confidential clients.
 * Since this is a redirection-based flow, the client must be capable of
 * interacting with the resource owner's user-agent (typically a web
 * browser) and capable of receiving incoming requests (via redirection)
 * from the authorization server.
 * <p>
 * +----------+
 * | Resource |
 * |   Owner  |
 * |          |
 * +----------+
 * ^
 * |
 * (B)
 * +----|-----+          Client Identifier      +---------------+
 * |         -+----(A)-- & Redirection URI ---->|               |
 * |  User-   |                                 | Authorization |
 * |  Agent  -+----(B)-- User authenticates --->|     Server    |
 * |          |                                 |               |
 * |         -+----(C)-- Authorization Code ---<|               |
 * +-|----|---+                                 +---------------+
 * |    |                                         ^      v
 * (A)  (C)                                        |      |
 * |    |                                         |      |
 * ^    v                                         |      |
 * +---------+                                      |      |
 * |         |>---(D)-- Authorization Code ---------'      |
 * |  Client |          & Redirection URI                  |
 * |         |                                             |
 * |         |<---(E)----- Access Token -------------------'
 * +---------+       (w/ Optional Refresh Token)
 * <p>
 * Note: The lines illustrating steps (A), (B), and (C) are broken into
 * two parts as they pass through the user-agent.
 * <p>
 * Figure 3: Authorization Code Flow
 *
 * <p>
 * 添加了`@EnableAuthorizationServer`注解之后，项目就可以当做一个授权服务提供商，给第三方应用提供oauth授权服务
 * <p>
 * `/oauth/authorize::GET`接口来提供oauth流程第一步，提供第三方服务获取“授权码”
 * <p>
 * 所需参数：
 * response_type
 * REQUIRED.  Value MUST be set to "code".
 * <p>
 * client_id
 * REQUIRED.  The client identifier as described in Section 2.2.
 * <p>
 * redirect_uri
 * OPTIONAL.  As described in Section 3.1.2.
 * <p>
 * <p>
 * `/oauth/authorize::GET`接口来提供oauth流程第一步，提供第三方服务获取“授权码”
 * <p>
 * `/oauth/token::POST`接口来提供oauth流程第而步，第三方服务通过“授权码”来获取授权令牌
 * <p>
 * 关于自定义生成Token
 * <p>
 * 当继承了{@link AuthorizationServerConfigurerAdapter}之后默认就不会生成默认的`clientId`和`secret`
 *
 * @author zhailiang
 * @see org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
 */
@Configuration
@EnableAuthorizationServer
public class CustomAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * https://stackoverflow.com/questions/29596036/spring-security-oauth2-resource-server-always-returning-invalid-token
     */
    static final String SERVER_RESOURCE_ID = "oauth2-server";

    /**
     * 当去做认证的时候使用的`pa`
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 当去做认证的时候使用的`authenticationManager`
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 负责令牌的存取
     */
    @Autowired
    private TokenStore tokenStore;

    /**
     * 自有{@link TokenStore}使用jwt进行存储时候生效
     */
    @Autowired(required = false)
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired(required = false)
    private TokenEnhancer jwtTokenEnhancer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 认证及token配置
     * 定义token增强器来自定义token的生成策略，覆盖{@link org.springframework.security.oauth2.provider.token.DefaultTokenServices}默认的UUID生成策略
     *
     * @see org.springframework.security.oauth2.provider.token.DefaultTokenServices#createAccessToken(OAuth2Authentication)
     * @see org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 当继承了`AuthorizationServerConfigurerAdapter`之后就需要自己配置下面的认证组件
        endpoints
                // 1.设置token存储器
                .tokenStore(tokenStore)
                // 2.支持用户名密码模式必须，授权默认中的用户名密码模式需要直接将认证凭证（用户名密码传递给授权服务器），授权服务器需要配置authenticationManager，去对这个用户进行身份认证
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
        // jwtAccessTokenConverter将token生成策略改成jwt，进行jwt的token生成（签名等）
        if (jwtAccessTokenConverter != null) {
            // 3.1自定义数据配置
            val enhancerChain = new TokenEnhancerChain();
            final List<TokenEnhancer> enhancers = new ArrayList<>();
            if (jwtTokenEnhancer != null) {
                // jwtTokenEnhancer向jwt token中订制自定义数据
                enhancers.add(jwtTokenEnhancer);
            }
            enhancers.add(jwtAccessTokenConverter);
            enhancerChain.setTokenEnhancers(enhancers);
            endpoints
                    .tokenEnhancer(enhancerChain)
                    // 3.设置token签名器
                    .accessTokenConverter(jwtAccessTokenConverter);
        }
    }


    /**
     * tokenKey的访问权限表达式配置
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // https://stackoverflow.com/questions/40699532/spring-security-with-oauth2-and-jwt-encoded-password-does-not-look-like-bcrypt
        security
//                .passwordEncoder(passwordEncoder);
                // 获取jwt的签名秘钥是否需要授权`isAuthenticated()`
                // tokenKey就是jwt的签名秘钥，第三方应用在获取到jwt token之后如果需要去验签就需要来服务端
                // 获取token key，配置`security.oauth2.resource.jet.key-uri`，在访问整个端点的时候需要带上客户端的clientId和clientSecret，因为下面配置成需要认证
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()");
//                .allowFormAuthenticationForClients();
        //enable client to get the authenticated when using the /oauth/token to get a access token
        //there is a 401 authentication is required if it doesn't allow form authentication for clients when access /oauth/token
//        security.allowFormAuthenticationForClients();
    }


    /**
     * 客户端配置
     * <p>
     * 当复写了该方法，默认的
     * <p>
     * security:
     * oauth2:
     * client:
     * client-id: immoc
     * client-secret: immocsecret
     * 配置将会失效
     * <p>
     * 需要自己根据配置应用支持的第三方应用client-id等应用信息
     *
     * @param clients 那些应用允许来进行token认证
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // clients.jdbc() 如果要实现qq那样提供授权信息给第三方最好使用这种模式，而下面的模式主要是针对`token`客户端登录的
        val builder = clients.inMemory();
        securityProperties.getOauth2().getClients()
                .forEach(client -> {
                    // 指定支持的第三方应用信息
                    builder
                            .withClient(client.getClientId())
                            // https://dzone.com/articles/securing-rest-services-with-oauth2-in-springboot-1
                            .secret(passwordEncoder.encode(client.getClientSecret()))
//                            .secret(client.getClientSecret())
                            // 可以直接配置获取授权码和授权token的第三方回调通知地址，如果配置就会用其校验获取授权code、码时候传递的redirect_uri
                            //.redirectUris()
                            // 针对当前第三方应用所支持的授权模式，即http://{{host}}/oauth/token#grant_type
                            // 还可以配置`implicit`简化模式/`client_credentials`客户端模式
                            .authorizedGrantTypes("refresh_token", "authorization_code", "password")
                            // 配置令牌的过期时间限，单位秒
                            .accessTokenValiditySeconds(client.getAccessTokenValidateSeconds())
                            // 配置refresh token的有效期，单位秒
                            .refreshTokenValiditySeconds(2592000)
//                            .resourceIds(SERVER_RESOURCE_ID)
                            // 针对当前第三方应用所支持的权限，即http://{{host}}/oauth/token#scope
                            // 说明应用需要的权限，发送请求的scope参数需要在此范围之内，不传就使用默认（即配置的值）
                            .scopes("all");
                });
    }

}
