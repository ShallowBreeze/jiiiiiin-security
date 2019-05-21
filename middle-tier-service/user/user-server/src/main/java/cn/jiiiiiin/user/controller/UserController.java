package cn.jiiiiiin.user.controller;

import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.service.IAdminService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiiiiiin
 */
@RestController
@RequestMapping("/user")
@Api
@AllArgsConstructor
@Slf4j
public class UserController {

    private final IAdminService adminService;

    /**
     * ==== 以下接口提供给远程服务间调用 ====
     */
    @GetMapping("/user/{channel}/{username}")
    Admin signInByUsernameOrPhoneNumbSimple(@PathVariable ChannelEnum channel, @PathVariable String username){
        log.debug("调用[UserController#signInByUsernameOrPhoneNumb]服务 channel: %s ,username: %s", channel, username);
        return adminService.signInByUsernameOrPhoneNumbSimple(channel, username);
    }

}
