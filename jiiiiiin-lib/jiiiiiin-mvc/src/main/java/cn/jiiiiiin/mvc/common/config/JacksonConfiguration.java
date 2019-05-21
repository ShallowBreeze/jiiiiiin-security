package cn.jiiiiiin.mvc.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author jiiiiiin
 */
@Configuration
public class JacksonConfiguration {

    /**
     * 解决`@JsonView`不生效的问题
     * https://blog.csdn.net/L_Sail/article/details/70217393
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }


//    /**
//     * http://blog.didispace.com/Spring-Boot-And-Feign-Use-localdate/
//     */
//    @Bean
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.registerModule(new JavaTimeModule());
//        return objectMapper;
//    }
}
