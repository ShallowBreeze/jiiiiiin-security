package cn.jiiiiiin.user.common.utils;

import lombok.val;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;

public class SpringMVC {

    /**
     * 一般启动
     * @param args
     * @param primarySources
     */
    public static void run(String[] args, Class<?>... primarySources) {
        val app = new SpringApplication(primarySources);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
