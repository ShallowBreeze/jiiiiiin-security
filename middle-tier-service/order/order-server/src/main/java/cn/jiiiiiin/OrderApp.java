package cn.jiiiiiin;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * http://localhost:7100/hystrix.stream 可以看到界面不断地输出监控日志，监控日志里包含了各种指标（各种指标信息请参考官方文档）
 *
 * @author jiiiiiin
 */
@RestController
@EnableApolloConfig
@EnableFeignClients
@SpringCloudApplication
@Configuration
public class OrderApp {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        val app = new SpringApplication(OrderApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @GetMapping("/")
    public String getTime() {
        return "The current time is " + new Date().toString() + "(answered by service running on " + port + ")";
    }

    /**
     * 如果需要开启Hystrix监控需要各个应用单独配置
     * TODO 抽取为基类，详细了解其作用
     * @return
     */
    @Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }

}
