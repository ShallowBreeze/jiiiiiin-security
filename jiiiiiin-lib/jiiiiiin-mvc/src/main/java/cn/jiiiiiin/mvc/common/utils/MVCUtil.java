package cn.jiiiiiin.mvc.common.utils;

import lombok.val;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jiiiiiin
 */
public class MVCUtil {

    /**
     * 一般启动
     *
     * @param args
     * @param primarySources
     */
    public static void run(String[] args, Class<?>... primarySources) {
        val app = new SpringApplication(primarySources);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


    /**
     * 获取 HttpServletRequest
     *
     * https://www.jianshu.com/p/80165b7743cf
     *
     * @return {HttpServletRequest}
     */
    @Nullable
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
