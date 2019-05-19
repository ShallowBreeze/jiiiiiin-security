package cn.jiiiiiin;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.jiiiiiin.mvc.common.utils.SpringMVC.run;

/**
 * @author jiiiiiin
 */
@Slf4j
//@EnableApolloConfig
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@MapperScan(value = {"cn.jiiiiiin.user.mapper"})
public class UserApplication {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        run(args, UserApplication.class);
    }

    @GetMapping("/testZuul")
    public R<String> testZuul() {
        return R.ok(String.format("你好Zuul网关 %s", port));
    }

}
