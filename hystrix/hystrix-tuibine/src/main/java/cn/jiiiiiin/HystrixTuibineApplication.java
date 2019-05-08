package cn.jiiiiiin;


import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * http://www.ityouknow.com/springcloud/2017/05/18/hystrix-dashboard-turbine.html
 *
 * @author jiiiiiin
 */
@EnableTurbine
@SpringCloudApplication
@Configuration
public class HystrixTuibineApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixTuibineApplication.class, args);
    }

    /**
     * 如果需要开启Hystrix监控需要各个应用单独配置
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

