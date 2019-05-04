package cn.jiiiiiin.module.common.advice;

import cn.jiiiiiin.module.common.annotation.IgnoreResponseAdvice;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * `@RestControllerAdvice`标识对响应进行统一拦截处理
 *
 * https://zhaoxuyang.com/springboot%E4%BD%BF%E7%94%A8swagger2%E5%87%BA%E7%8E%B0unable-to-infer-base-url-this-is-common-when-using-dynamic-servlet-registration-or-when-the-api-is-behind-an-api-gateway/
 * 4.但是在我的项目中，发现是我使用Spring的ResponseBodyAdvice全局返回再处理的一个类，本意是为所有返回JSON数据统一添加“状态=succuess”等信息，没想到该实现影响了Swagger的使用，会导致swagger返回的JSON数据格式和期望的不一致，故swagger报错。解决方法在该接口实现类上面的@ControllerAdvice 注解限制接口的扫描包即可避免。
 * @author jiiiiiin
 */
@RestControllerAdvice(basePackages = "cn.jiiiiiin")
@Slf4j
@AllArgsConstructor
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        R<Object> response = null;
        // 解决：`https://my.oschina.net/u/1757225/blog/1543715`
        if (o instanceof String) {
            try {
                return objectMapper.writeValueAsString(R.ok(o));
            } catch (JsonProcessingException e) {
                log.error("转换String类型的服务器端响应数据出错", e);
            }
        }
        if (o instanceof R) {
            response = (R<Object>) o;
        } else if (null == o) {
            response = R.ok("服务端无数据返回");
        } else {
            response = R.ok(o);
        }
        log.debug("服务器端响应数据 {}", response);
        return response;
    }
}
