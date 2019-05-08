package cn.jiiiiiin.hystrix;

import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 网关服务降级
 * <p>
 * https://blog.csdn.net/WYA1993/article/details/82770827
 * <p>
 *
 * @author jiiiiiin
 */
@Slf4j
@AllArgsConstructor
@Component
public class ZuulFallbackProvider implements FallbackProvider {

    private final ObjectMapper objectMapper;

    /**
     * 指明拦截你需要降级的服务名，如果需要所有调用都支持回退，则return "*"或return null。
     *
     * @return
     */
    @Override
    public String getRoute() {
        // 表明是为哪个微服务提供回退，*表示为所有微服务提供回退
        return "*";
    }

    /**
     * 定制返回内容
     * 如果请求用户服务失败，返回什么信息给消费者客户端
     * 该方法会让我定义一个ClientHttpResponse作为当异常出现时的返回内容。
     *
     * @param route
     * @param cause 也就是说此时是用能力获取异常信息的
     * @return
     */
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause != null) {
            log.error("网关收到降级错误", cause);
        }
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ClientHttpResponse response(final HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return status;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return status.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                log.debug("网关服务降级执行");
                return new ByteArrayInputStream(objectMapper.writeValueAsString(R.failed("服务不可用，请稍后再试")).getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                // headers设定
                HttpHeaders headers = new HttpHeaders();
                // 和body中的内容编码一致，否则容易乱码
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }
        };
    }
}
