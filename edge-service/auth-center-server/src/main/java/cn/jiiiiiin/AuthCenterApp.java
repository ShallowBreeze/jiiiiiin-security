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
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
//@EnableApolloConfig
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@AllArgsConstructor
// TODO 待把用户抽成一个原子服务
@MapperScan(value = {"cn.jiiiiiin.user.mapper"})
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
