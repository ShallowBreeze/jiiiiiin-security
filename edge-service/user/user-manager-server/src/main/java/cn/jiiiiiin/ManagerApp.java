package cn.jiiiiiin;

import com.baomidou.mybatisplus.extension.api.R;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;

/**
 * @author jiiiiiin
 */
@Slf4j
@EnableTransactionManagement
@EnableApolloConfig
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@MapperScan(value = {"cn.jiiiiiin.user.mapper"})
public class ManagerApp {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        val app = new SpringApplication(ManagerApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @GetMapping("/testZuul")
    public R<String> testZuul() {
        return R.ok(String.format("你好Zuul网关 %s", port));
    }

}
