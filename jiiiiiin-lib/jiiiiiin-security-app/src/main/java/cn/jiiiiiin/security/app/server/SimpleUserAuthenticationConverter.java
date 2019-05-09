package cn.jiiiiiin.security.app.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Map;

@Slf4j
public class SimpleUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        throw new RuntimeException("请配置自己的SimpleUserAuthenticationConverter");
    }
}
