package cn.jiiiiiin.security.core.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiiiiiin
 * @ConfigurationProperties(prefix = "jiiiiiin.security") 将会读取配置文件中对应prefix的配置项
 */
@ConfigurationProperties(prefix = "jiiiiiin.security")
@Setter
@Getter
@NoArgsConstructor
@RefreshScope
@ToString
@Slf4j
@Component
public class SecurityProperties {

    /**
     * 公共接口，会配置到SpringSecurity安全控制使用`permitAll`表达式
     * 如：`securityProperties.getPublicApi().forEach(url -> config.antMatchers(url).permitAll());`
     */
    private List<String> publicApi = new ArrayList<>();

    /**
     * 浏览器环境配置
     */
    private BrowserProperties browser = new BrowserProperties();
    /**
     * 验证码配置
     */
    private ValidateCodeProperties validate = new ValidateCodeProperties();

    /**
     * 社交登录配置
     */
    private CustomSocialProperties social = new CustomSocialProperties();

    /**
     * OAuth2认证服务器配置
     */
    private OAuth2Properties oauth2 = new OAuth2Properties();

    @PostConstruct
    private void initialize() {
        log.info("SecurityProperties initialized - browser: {}, validate: {}, social: {}, oauth2: {}", browser, validate, social, oauth2);
    }
}
