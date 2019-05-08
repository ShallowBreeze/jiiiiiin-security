package cn.jiiiiiin.controller;

import cn.jiiiiiin.product.client.FeignClientTest;
//import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author jiiiiiin
 */
@RestController
@Slf4j
// 2. 针对整个控制器进行服务降级接口的声明
//@DefaultProperties(defaultFallback = "defaultFallback")
public class ServiceCallTestController {

    private static final String PRODUCT_SERVICE_ID = "JIIIIIIN-PRODUCT";

    @Value("${server.port}")
    private int port;

    /**
     * 通过LB去eureka server获取到的服务地址集合做软负载
     */
    private final LoadBalancerClient loadBalancerClient;

    private final RestTemplate restTemplate;

    private final FeignClientTest feignClientTest;

    @Autowired
    public ServiceCallTestController(LoadBalancerClient loadBalancerClient, RestTemplate restTemplate, FeignClientTest feignClientTest) {
        this.loadBalancerClient = loadBalancerClient;
        this.restTemplate = restTemplate;
        this.feignClientTest = feignClientTest;
    }

    // 测试熔断：curl "http://localhost:7100/msg?sw=1"
    // + feign client 配置的服务降级只是针对其本身 ！！！ 如果使用feign就可以不设置`@HystrixCommand`，直接使用client上面提供的降级配置，注意这里只是指降级对应client的
    // + 添加了`spring-boot-starter-actuator`就可以使用`http://hystrix-app:port/hystrix.stream `查看自身的熔断指标
    // ## 针对单个接口进行服务降级接口的声明
    // + 需要进行熔断必须配置`@HystrixCommand`
    // + 如果要实现服务降级，就算使用默认配置`fallbackMethod`，也需要在接口上面配置`@HystrixCommand`
    // http://blog.didispace.com/springcloud3/
    @HystrixCommand(fallbackMethod = "fallback",
            // `HystrixCommandProperties`查看更多配置信息
            commandProperties = {
            // 配置超时时间，默认为1秒，这里设置为3秒
            // 超时时间要看具体接口的调用执行逻辑，根据情况来设置
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000")
    })
//
//    @HystrixCommand(fallbackMethod = "fallback",
//            commandProperties = {
//                // HystrixProperty 更多配置，可以查看`HystrixCommandProperties`
//                // 设置开启熔断功能，默认开启
//                    // 流量阈值和失败率阈值都满足才会开启熔断
//                @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
//                    // 流量阈值
//                    // 当在配置时间窗口内达到此数量的失败后，进行短路，默认20个/每秒
//                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
//                    // 失败率阈值
//                    //出错百分比阈值，当达到此阈值后，开始短路，默认50%
//                @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),
//                    // 休眠时间窗，短路多久以后开始尝试是否恢复，默认5s
//                @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
//                    // 上面的配置就是在说：在10次调用中，失败率超过60%，即7次，那么就会发生熔断，在间隔10秒将会进入半熔断进行放量测试
//
//            }
//    )
    // 4.定制线程池隔离策略
//    @HystrixCommand(fallbackMethod = "fallback",
//            threadPoolKey = "orderServiceThreadPool",
//            threadPoolProperties = {
//                    // 线程池最大数量，默认是基于线程池隔离
//                    @HystrixProperty(name="coreSize", value="30"),
//                    // 池外队列数量
//                    @HystrixProperty(name="maxQueueSize", value="10")
//            }
//    )
    // 2.定制超时
//    @HystrixCommand(commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
    // 如果服务延迟，大于默认1000 :: {"timestamp":"2019-03-06T03:00:20.627+0000","status":500,"error":"Internal Server Error","message":"getOrder timed-out and fallback failed.","path":"/msg"}%
    // 如果服务调用过程中出现错误：java.lang.RuntimeException: 模拟商品服务 则:: {"timestamp":"2019-03-06T03:00:36.283+0000","status":500,"error":"Internal Server Error","message":"500 null","path":"/msg"}%
    // 3.定制降级方法
    // 如果服务延迟，大于默认1000 ::断路器生效了【fallback】%
    // 如果服务调用过程中出现错误：java.lang.RuntimeException: 模拟商品服务 则::断路器生效了【fallback】%
//    @HystrixCommand(fallbackMethod = "fallback")
    // 1.简单实用
    // 如果服务延迟，大于默认1000 :: {"timestamp":"2019-03-06T02:42:23.684+0000","status":500,"error":"Internal Server Error","message":"getOrder timed-out and fallback failed.","path":"/msg"}%
//    @HystrixCommand
    @GetMapping("/msg")
    public String getOrder(@RequestParam(value = "sw", required = false) Integer sw) {
        // 模拟熔断，开关
        if(sw!= null && sw % 2 == 0){
            return "success";
        }
        val templ = new RestTemplate();

        // 1.第一种通讯方式（直接使用restTemplate，url写死）
//        val product = templ.getForObject("http://localhost:7000/msg", String.class);
//        val product = restTemplate.getForObject("http://localhost:7000/product/msg", String.class);

        // 2.第二种方式
        // loadBalancerClient帮我们做客户端软负载
//        val serviceInstance = loadBalancerClient.choose(PRODUCT_SERVICE_ID);
//        val url = String.format("http://%s:%s/product/msg", serviceInstance.getHost(), serviceInstance.getPort());
//        val product = templ.getForObject(url, String.class);

        // 3.第三种方式
        val product = restTemplate.getForObject(String.format("http://%s/msg", PRODUCT_SERVICE_ID), String.class);

        // 4.第四种方式
//        val product = feignClientTest.getMsg();
        log.info("getOrder:: product {}", product);

//        throw new RuntimeException("服务降级测试触发");

        // print -> `订单7100::获取到商品7000`
        return "订单" + port + " :: " + product;
    }

    private String fallback(@RequestParam(value = "sw", required = false) Integer sw) {
        return "order断路器生效了【fallback】";
    }

    private String defaultFallback(@RequestParam(value = "sw", required = false) Integer sw) {
        return "order断路器生效了【defaultFallback】";
    }
}
