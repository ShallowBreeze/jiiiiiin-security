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
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.jiiiiiin.user.common.utils.SpringMVC.run;

/**
 * @author jiiiiiin
 */
@Slf4j
//@EnableApolloConfig
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@MapperScan(value = {"cn.jiiiiiin.user.mapper"})
public class UserApp {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        run(args, UserApp.class);
    }

    @GetMapping("/testZuul")
    public R<String> testZuul() {
        return R.ok(String.format("你好Zuul网关 %s", port));
    }

}
