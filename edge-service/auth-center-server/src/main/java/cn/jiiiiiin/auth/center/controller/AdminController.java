package cn.jiiiiiin.auth.center.controller;


import cn.jiiiiiin.data.entity.BaseEntity;
import cn.jiiiiiin.mvc.common.utils.View;
import cn.jiiiiiin.mvc.common.validation.Groups;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.vo.AdminDto;
import cn.jiiiiiin.user.vo.CommonUserDetails;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@RestController
@RequestMapping("/user")
@Api
@AllArgsConstructor
@Slf4j
public class AdminController {

//    private final SimpleGrantedAuthority adminGrantedAuthority;

    /**
     * 获取当前登录的管理员信息
     * <p>
     * 注意：
     * browser模块可以直接注入： public AdminDto me(@AuthenticationPrincipal UserDetails user) {
     * <p>
     * app模块 jwt生成的就不是一个可以被注入的`@AuthenticationPrincipal`
     */
    @ApiOperation(value = "登录用户记录查询", httpMethod = "GET")
    @JsonView(View.DetailView.class)
    @GetMapping("me")
    public Admin me(Authentication authentication) {
        log.debug("登录用户记录查询 {}", authentication);
        CommonUserDetails mngUserDetails = (CommonUserDetails) authentication.getPrincipal();
        return mngUserDetails.getAdmin();
    }

//    @ApiOperation(value = "更新用户密码", notes = "只能在用户拥有系统管理员角色权限的状态下使用该接口", httpMethod = "PUT")
//    @PutMapping("/pwd")
//    @JsonView(View.SecurityView.class)
//    public AdminDto updatePwd(@RequestBody @Validated({BaseEntity.IDGroup.class, Groups.Security.class}) AdminDto admin, @AuthenticationPrincipal UserDetails user) {
//        if (user.getAuthorities().stream().anyMatch(p -> p.equals(adminGrantedAuthority))) {
//            // TODO 调用`user-server`服务
//            //adminService.updatePwd(admin);
//        }
//        return admin;
//    }

//    @ApiOperation(value = "批量删除用户记录", notes = "根据路径参数解析待删除的用户记录id集合，登录用户自身不能删除自己的记录", httpMethod = "DELETE")
//    @DeleteMapping("dels/{ids:^[\\d,]+$}")
//    public void dels(@PathVariable String ids) {
//        _checkHasSelf(ids.split(","));
//        // TODO 调用`user-server`服务
//        //adminService.removeAdminsAndRelationRecords(ids);
//    }
//
//    @ApiOperation(value = "删除用户记录", notes = "根据路径参数用户记录id删除对应用户，登录用户自身不能删除自己的记录", httpMethod = "DELETE")
//    @DeleteMapping("{id:\\d}")
//    public void del(@PathVariable Long id) {
//        _checkHasSelf(new String[]{String.valueOf(id)});
//        // TODO 调用`user-server`服务
//        //adminService.removeAdminAndRelationRecord(id);
//    }
//
//    /**
//     * 检测待操作的用户是否包含登录用户自身，如果存在不允许操作
//     *
//     * @param ids
//     */
//    private void _checkHasSelf(String[] ids) {
//        val userDetails = (MngUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (ArrayUtils.contains(ids, String.valueOf(userDetails.getAdmin().getId()))) {
//            throw new UserServiceException("当前接口服务，不允许操作登录用户自己");
//        }
//    }

}
