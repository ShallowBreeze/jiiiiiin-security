package cn.jiiiiiin.security.core.config;

import cn.jiiiiiin.security.core.dict.CommonConstants;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.*;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * https://docs.spring.io/spring-mobile/docs/current/reference/html/device.html
 *
 * @author jiiiiiin
 */
@Configuration
public class SpringMobileConfig implements WebMvcConfigurer {

    /**
     * 拦截请求，配置spring mobile支持的渠道解析拦截器
     *
     * @return
     */
    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
        return new DeviceResolverHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceResolverHandlerInterceptor());
    }

    /**
     * If you'd like to pass the current Device as an argument to one of your @Controller methods, configure a DeviceWebArgumentResolver:
     * You can then inject the Device into your @Controllers
     */
    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
        return new DeviceHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(deviceHandlerMethodArgumentResolver());
    }


    /**
     * https://docs.spring.io/spring-mobile/docs/current/reference/html/device.html
     *
     * https://github.com/spring-projects/spring-mobile-samples/blob/22896c9d57e49261be28921fb16ed763a310bd6a/lite-device-resolver/src/test/java/showcases/HomeControllerTest.java
     * @return
     */
    @Bean
    public LiteDeviceResolver liteDeviceResolver() {
        List<String> keywords = new ArrayList<String>();
        // TODO 后期做成配置
        keywords.add("iphone");
        keywords.add("android");
        return new LiteDeviceResolver(keywords){
            @Override
            public Device resolveDevice(HttpServletRequest request) {
                // 有限判断accept请求头
                val accept = request.getHeader("accept");
                // Accept-header based detection
                if (accept != null && (accept.contains(CommonConstants.ACCEPT_JSON_PREFIX) || accept.contains("wap"))) {
                    return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.UNKNOWN);
                } else {
                    return super.resolveDevice(request);
                }
            }
        };
    }

}
