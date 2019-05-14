package cn.jiiiiiin.user.client;

import cn.jiiiiiin.user.UserDict;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.exception.UserServiceException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static cn.jiiiiiin.user.UserDict.formatServiceFallbackMsg;

/**
 * @author jiiiiiin
 */
@FeignClient(name = UserDict.SERVICE_NAME, fallbackFactory = RemoteUserService.FeignClientFallBack.class)
public interface RemoteUserService {

    /**
     * @param channel  对应的应用渠道
     * @param username 用户名或者手机号
     * @return 系统标准用户
     */
    @GetMapping("/admin/{channel}/{username}")
    Admin signInByUsernameOrPhoneNumb(@PathVariable ChannelEnum channel, @PathVariable String username);

    @Component
    @Slf4j
    class FeignClientFallBack implements FallbackFactory<RemoteUserService> {
        @Override
        public RemoteUserService create(Throwable cause) {
            if (cause != null) {
                log.error(UserDict.SERVICE_NAME + "服务发生服务降级", cause);
            }
            return new RemoteUserService() {
                @Override
                public Admin signInByUsernameOrPhoneNumb(ChannelEnum channel, String username) {
                    log.error(UserDict.SERVICE_NAME+"#signInByUsernameOrPhoneNumb降级方法被执行");
                    return null;
                }
            };
        }
    }
}
