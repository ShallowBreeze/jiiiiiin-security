package cn.jiiiiiin.user.client;

import cn.jiiiiiin.user.UserDict;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.exception.UserServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static cn.jiiiiiin.user.UserDict.formatServiceFallbackMsg;

@FeignClient(name = UserDict.SERVICE_NAME, fallback = RemoteUserService.FeignClientFallBack.class)
public interface RemoteUserService {

    Logger log = LoggerFactory.getLogger(RemoteUserService.class);

    @Component
    class FeignClientFallBack implements RemoteUserService {
    }

    /**
     * @param channel  对应的应用渠道
     * @param username 用户名或者手机号
     * @return 系统标准用户
     */
    @GetMapping("/admin/{channel}/{username}")
    default Admin signInByUsernameOrPhoneNumb(@PathVariable ChannelEnum channel, @PathVariable String username) {
        log.debug("signInByUsernameOrPhoneNumb 服务被降级");
        throw new UserServiceException(formatServiceFallbackMsg("signInByUsernameOrPhoneNumb"));
    }
}
