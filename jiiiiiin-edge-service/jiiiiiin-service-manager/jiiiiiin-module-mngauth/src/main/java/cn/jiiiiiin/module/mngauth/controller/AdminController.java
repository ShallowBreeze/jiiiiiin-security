package cn.jiiiiiin.module.mngauth.controller;


import cn.jiiiiiin.data.orm.entity.BaseEntity;
import cn.jiiiiiin.data.orm.util.View;
import cn.jiiiiiin.module.common.dto.mngauth.AdminDto;
import cn.jiiiiiin.module.common.entity.mngauth.Admin;
import cn.jiiiiiin.module.common.enums.common.ChannelEnum;
import cn.jiiiiiin.module.common.exception.BusinessErrException;
import cn.jiiiiiin.module.common.validation.Groups;
import cn.jiiiiiin.module.mngauth.component.MngUserDetails;
import cn.jiiiiiin.module.mngauth.service.IAdminService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

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

    private final SimpleGrantedAuthority adminSimpleGrantedAuthority;

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
        return adminService.pageAdminDto(new Page<AdminDto>(current, size), channel, admin);
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
        return adminService.page(new Page<Admin>(current, size), qw);
    }


    /**
     * 获取当前登录的管理员信息
     */
    @ApiOperation(value = "登录用户自身记录查询", httpMethod = "GET")
    @JsonView(View.DetailView.class)
    @GetMapping("/me")
//    public AdminDto me(@AuthenticationPrincipal UserDetails user) {
    // 使用app模块 jwt生成的就不是一个可以被注入的`@AuthenticationPrincipal`
    public AdminDto me(Authentication authentication) {
//        return (AdminDto) new AdminDto().setUsername(user.getUsername());
        // TODO 待测试
        return (AdminDto) new AdminDto().setUsername(authentication.getPrincipal().toString());
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

    @ApiOperation(value = "更新用户密码", notes = "只能在用户拥有系统管理员角色权限的状态下使用该接口", httpMethod = "PUT")
    @PutMapping("pwd")
    @JsonView(View.SecurityView.class)
    public AdminDto updatePwd(@RequestBody @Validated({BaseEntity.IDGroup.class, Groups.Security.class}) AdminDto admin, @AuthenticationPrincipal UserDetails user) {
        if (user.getAuthorities().stream().anyMatch(p -> p.equals(adminSimpleGrantedAuthority))) {
            adminService.updatePwd(admin);
        }
        return admin;
    }

    @ApiOperation(value = "批量删除用户记录", notes = "根据路径参数解析待删除的用户记录id集合，登录用户自身不能删除自己的记录", httpMethod = "DELETE")
    @DeleteMapping("dels/{ids:^[\\d,]+$}")
    public void dels(@PathVariable String ids) {
        _checkHasSelf(ids.split(","));
        adminService.removeAdminsAndRelationRecords(ids);
    }

    /**
     * 检测待操作的用户是否包含登录用户自身，如果存在不允许操作
     *
     * @param ids
     */
    private void _checkHasSelf(String[] ids) {
        val userDetails = (MngUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (ArrayUtils.contains(ids, String.valueOf(userDetails.getAdmin().getId()))) {
            throw new BusinessErrException("当前接口服务，不允许操作登录用户自己");
        }
    }

    @ApiOperation(value = "删除用户记录", notes = "根据路径参数用户记录id删除对应用户，登录用户自身不能删除自己的记录", httpMethod = "DELETE")
    @DeleteMapping("{id:\\d}")
    public void del(@PathVariable Long id) {
        _checkHasSelf(new String[]{String.valueOf(id)});
        adminService.removeAdminAndRelationRecord(id);
    }

}
