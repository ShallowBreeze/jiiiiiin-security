package cn.jiiiiiin;

import cn.jiiiiiin.security.core.properties.SecurityProperties;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@EnableApolloConfig
@Slf4j
@EnableFeignClients
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableDiscoveryClient
@RestController
@AllArgsConstructor
public class AuthCenterApp {

    private final SecurityProperties securityProperties;

    public static void main(String[] args) {
        val app = new SpringApplication(AuthCenterApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
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
