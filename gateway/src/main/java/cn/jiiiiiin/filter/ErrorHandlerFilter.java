package cn.jiiiiiin.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_ERROR_FILTER_ORDER;

/**
 * TODO 全局异常处理器，处理之后会交由SendErrorFilter完成后续输出
 * <p>
 * 参考:http://blog.didispace.com/spring-cloud-zuul-exception/
 *
 * @author jiiiiiin
 */
public class ErrorHandlerFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        // 在SendErrorFilter内置错误处理之前执行
        return SEND_ERROR_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        return null;
    }
}
