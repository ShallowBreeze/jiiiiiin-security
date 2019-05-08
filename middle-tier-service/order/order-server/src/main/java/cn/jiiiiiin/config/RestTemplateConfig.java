package cn.jiiiiiin.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author jiiiiiin
 */
@Configuration
public class RestTemplateConfig {

    /**
     * LoadBalanced 底层依赖Ribbon
     * 1、实现服务发现
     * 2、服务选择规则
     * 3、服务监听（剔除、添加）
     *
     * [ServerList IRule ServerListFilter](https://coding.imooc.com/lesson/187.html#mid=11784)
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
