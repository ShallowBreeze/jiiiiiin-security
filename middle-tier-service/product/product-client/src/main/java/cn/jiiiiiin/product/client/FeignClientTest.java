package cn.jiiiiiin.product.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * `RestTemplateTestServerController`的Feign接口
 * 1.name标识代理的服务名
 * 2.fallback 如果产生服务降级，将会访问对应bean::接口
 * <p>
 * 调用方必须配置：
 * <p>
 * feign:
 * hystrix:
 * # Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开。需要在配置文件中配置打开它，在配置文件加以下代码
 * enabled: true
 *
 * 如果产生服务降级：这里体现在熔断监控里面只是`紫色计数:表示因为线程池满而被拒绝的请求数`
 * @author jiiiiiin
 */
@FeignClient(name = "jiiiiiin-product", fallback = FeignClientTest.FeignClientTestFallBack.class)
//@FeignClient(name = "jiiiiiin-product")
public interface FeignClientTest {

    Logger log = LoggerFactory.getLogger(FeignClientTest.class);

    /**
     * 注意如果被调用的服务设置了上下文，那么接口必须手动添加上下文
     *
     * @return
     */
    @GetMapping("/msg")
    default String getMsg() {
        log.debug("client getMsg");
        return "服务降级【product服务返回】";
    }

    @Component
    class FeignClientTestFallBack implements FeignClientTest {
    }
}
