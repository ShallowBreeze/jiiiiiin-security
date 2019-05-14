package cn.jiiiiiin.product.fallback;

import cn.jiiiiiin.product.client.FeignClientTest;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author jiiiiiin
 */
@Component
@Slf4j
public class FeignClientTestFallBack implements FallbackFactory<FeignClientTest> {

//    @Override
//    public String getMsg() {
//        log.debug("client getMsg");
//        return "product-server#getMsg服务发生服务降级";
//    }

    @Override
    public FeignClientTest create(Throwable cause) {
        if (cause != null) {
            log.error("product-server#getMsg服务发生服务降级", cause);
        }
        return new FeignClientTest() {
            @Override
            public String getMsg() {
                return null;
            }

            @Override
            public String getMsg2() {
                return null;
            }
        };
    }
}
