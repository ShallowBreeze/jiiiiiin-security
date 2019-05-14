package cn.jiiiiiin.user.controller;


import cn.jiiiiiin.data.entity.BaseEntity;
import cn.jiiiiiin.user.common.exception.BusinessErrException;
import cn.jiiiiiin.user.common.utils.View;
import cn.jiiiiiin.user.common.validation.Groups;
import cn.jiiiiiin.user.dto.AdminDto;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.service.IAdminService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;


/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@RestController
@RequestMapping("/admin")
@Api
@AllArgsConstructor
@Slf4j
public class AdminController extends BaseController {

    private final IAdminService adminService;

    @ApiOperation(value = "第三方授权登录注册/绑定用户", notes = "关联的角色记录，必须传递到{@link AdminDto#roleIds}字段中", httpMethod = "POST")
    @PostMapping("regist")
    @JsonView(View.DetailView.class)
    public AdminDto regist(@RequestBody @Validated({Groups.Create.class, Groups.Security.class, Default.class}) AdminDto admin, HttpServletRequest request) {
        // 针对内管的用户注册，将会给定一个特殊的角色，该角色不会赋予任何权限，
        // 手动注册的用户需要由系统管理员审核之后二次分发权限
//        admin.setRoleIds(new String[]{"1112903286077321218"});
//        // 默认为内管渠道
//        admin.setChannel(ChannelEnum.MNG);
        adminService.regist(admin, request);
        log.debug("第三方授权登录注册/绑定用户 {}", admin);
        return admin;
    }

    @ApiOperation(value = "用户记录分页查询", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<AdminDto> list(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size) {
        return adminService.pageAdminDto(new Page<>(current, size), channel, null);
    }

    @ApiOperation(value = "用户【AdminDto】记录分页检索", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @PostMapping("search/dto/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<AdminDto> searchAdminDto(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size, @RequestBody AdminDto admin) {
        return adminService.pageAdminDto(new Page<>(current, size), channel, admin);
    }

    @ApiOperation(value = "用户记录分页检索", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @PostMapping("search/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<Admin> search(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size, @RequestBody Admin admin) {
        val qw = new QueryWrapper<Admin>()
                .eq(Admin.CHANNEL, channel);
        if (StringUtils.isNotEmpty(admin.getUsername())) {
            qw.like(Admin.USERNAME, admin.getUsername());
        }
        if (StringUtils.isNotEmpty(admin.getPhone())) {
            qw.like(Admin.PHONE, admin.getPhone());
        }
        if (StringUtils.isNotEmpty(admin.getEmail())) {
            qw.like(Admin.EMAIL, admin.getEmail());
        }
        return adminService.page(new Page<>(current, size), qw);
    }

    @ApiOperation(value = "用户记录查询", httpMethod = "GET")
    @GetMapping("{id:\\d+}")
    @JsonView(View.DetailView.class)
    public AdminDto getAdminAndRelationRecords(@PathVariable Long id) {
        return adminService.getAdminAndRelationRecords(id);
    }

    @ApiOperation(value = "新增用户", notes = "关联的角色记录，必须传递到{@link AdminDto#roleIds}字段中", httpMethod = "POST")
    @PostMapping
    @JsonView(View.DetailView.class)
    public AdminDto create(@RequestBody @Validated({Groups.Create.class, Groups.Security.class, Default.class}) AdminDto admin) {
        if (adminService.saveAdminAndRelationRecords(admin)) {
            return admin;
        } else {
            throw new BusinessErrException("添加用户失败");
        }
    }

    @ApiOperation(value = "更新用户信息", notes = "关联的角色记录，必须传递到{@link AdminDto#roleIds}字段中", httpMethod = "PUT")
    @PutMapping
    @JsonView(View.DetailView.class)
    public AdminDto update(@RequestBody @Validated({BaseEntity.IDGroup.class, Groups.Create.class, Default.class}) AdminDto admin) {
        if (adminService.updateAdminAndRelationRecords(admin)) {
            return admin;
        } else {
            throw new BusinessErrException("修改用户记录失败");
        }
    }

    /**
     * 删除的权限控制请在边界服务*做好*
     *
     * @param admin
     * @return
     */
    @ApiOperation(value = "更新用户密码", notes = "只能在用户拥有系统管理员角色权限的状态下使用该接口", httpMethod = "PUT")
    @PutMapping("pwd")
    @JsonView(View.SecurityView.class)
    public Boolean updatePwd(@RequestBody @Validated({BaseEntity.IDGroup.class, Groups.Security.class}) AdminDto admin) {
        return adminService.updatePwd(admin);
    }

    @ApiOperation(value = "批量删除用户记录", notes = "根据路径参数解析待删除的用户记录id集合，登录用户自身不能删除自己的记录", httpMethod = "DELETE")
    @DeleteMapping("dels/{ids:^[\\d,]+$}")
    public Boolean dels(@PathVariable String ids) {
        return adminService.removeAdminsAndRelationRecords(ids);
    }

    /**
     * ==== 以下接口提供给远程服务间调用 ====
     */
    @GetMapping("/{channel:[0]}/{username}")
    public Admin signInByUsernameOrPhoneNumb(@PathVariable ChannelEnum channel, @PathVariable String username) {
        log.debug("调用[signInByUsernameOrPhoneNumb]服务 channel: %s ,username: %s", channel, username);
        return adminService.signInByUsernameOrPhoneNumb(username, channel);
    }
}
