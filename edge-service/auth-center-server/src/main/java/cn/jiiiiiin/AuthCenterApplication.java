package cn.jiiiiiin;

import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.user.common.utils.SpringMVC;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiiiiiin
 */ //@EnableApolloConfig
@Slf4j
@EnableFeignClients
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableDiscoveryClient
@RestController
@AllArgsConstructor
public class AuthCenterApplication {

    private final SecurityProperties securityProperties;

    public static void main(String[] args) {
        SpringMVC.run(args, AuthCenterApplication.class);
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return String.format("请访问 %s", securityProperties.getBrowser().getFontUrl());
    }

    @GetMapping("/testZuul")
    public String testZuul() {
        return "你好Zuul网关";
    }

}
