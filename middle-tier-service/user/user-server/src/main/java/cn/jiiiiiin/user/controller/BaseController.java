package cn.jiiiiiin.user.controller;

import cn.jiiiiiin.user.enums.ChannelEnum;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author jiiiiiin
 */
public class BaseController {


    /**
     * 解决接口`path variable`转换枚举问题
     * https://www.devglan.com/spring-boot/enums-as-request-parameters-in-spring-boot-rest
     *
     * @param webdataBinder
     */
    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(ChannelEnum.class, new ChannelEnum.ResourceChannelEnumConverter());
    }
}
