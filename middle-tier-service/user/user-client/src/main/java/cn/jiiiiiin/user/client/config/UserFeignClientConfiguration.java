package cn.jiiiiiin.user.client.config;

import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 解决feign传递枚举值的问题
 * 原文：https://blog.csdn.net/u014527058/article/details/79396998
 * @author jiiiiiin
 */
@Configuration
@Slf4j
public class UserFeignClientConfiguration {

//    @Autowired(required = false)
//    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    // TODO 待研究feign如何适配枚举类型
//    @Bean
//    public Contract feignContract(FormattingConversionService feignConversionService) {
//        log.debug("UserFeignClientConfiguration#feignContract执行");
//        //在原配置类中是用ConversionService类型的参数，但ConversionService接口不支持addConverter操作，使用FormattingConversionService仍然可以实现feignContract配置。
//        feignConversionService.addConverter(new ChannelEnumConverter());
//        return new SpringMvcContract(this.parameterProcessors, feignConversionService);
//    }
}
