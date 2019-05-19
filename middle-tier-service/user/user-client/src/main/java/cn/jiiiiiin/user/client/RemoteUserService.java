package cn.jiiiiiin.user.client;

import cn.jiiiiiin.user.UserDict;
import cn.jiiiiiin.user.client.config.UserFeignClientConfiguration;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author jiiiiiin
 */
@FeignClient(name = UserDict.SERVICE_NAME, configuration = UserFeignClientConfiguration.class, fallbackFactory = RemoteUserService.FeignClientFallBack.class)
public interface RemoteUserService {

    /**
     * 通过用户名&手机号获取对应渠道的用户信息
     *
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
                log.error(UserDict.SERVICE_NAME + "发生服务降级", cause);
            }
            return new RemoteUserService() {
                @Override
                public Admin signInByUsernameOrPhoneNumb(ChannelEnum channel, String username) {
                    log.error(UserDict.SERVICE_NAME + "#signInByUsernameOrPhoneNumb降级方法被执行");
                    return null;
                }
            };
        }
    }
}
