package cn.jiiiiiin;

import com.netflix.discovery.DiscoveryManager;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author jiiiiiin
 */
@SpringCloudApplication
@RestController
public class ProductApp {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        val app = new SpringApplication(ProductApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    /**
     * 实现服务剔除
     */
    @GetMapping("/pullOut")
    public void servicePullOut() {
        // TODO 待掌握如何提出服务，还是只能依靠DevOps？
        DiscoveryManager.getInstance().shutdownComponent();
    }

    @GetMapping("/")
    public String getTime() {
        return "The current time is " + new Date().toString() + "(answered by service running on " + port + ")";
    }

}
