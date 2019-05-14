package cn.jiiiiiin.user.controller;


import cn.jiiiiiin.data.entity.BaseEntity;
import cn.jiiiiiin.user.common.utils.View;
import cn.jiiiiiin.user.dto.RoleDto;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.entity.Role;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Slf4j
@RestController
@RequestMapping("/role")
@Api
public class RoleController extends BaseController {

    @Autowired
    private IRoleService roleService;

    @ApiOperation(value = "角色记录列表查询", notes = "查询对应渠道所有角色记录", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("list/{channel:[0]}")
    public List<Role> page(@PathVariable ChannelEnum channel) {
        return roleService.list(new QueryWrapper<Role>().eq(Role.CHANNEL, channel));
    }

    @ApiOperation(value = "角色记录分页查询", notes = "分页查询对应渠道角色记录", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<Role> page(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size) {
        return roleService.page(new Page<>(current, size), new QueryWrapper<Role>().eq(Role.CHANNEL, channel));
    }

    @ApiOperation(value = "角色记录分页检索", notes = "分页检索对应渠道角色记录", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @PostMapping("search/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<Role> searchPage(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size, @RequestBody Role role) {
        return roleService.page(new Page<>(current, size), new QueryWrapper<Role>().eq(Role.CHANNEL, channel).like(Role.AUTHORITY_NAME, role.getAuthorityName()));
    }

    @ApiOperation(value = "角色记录（适配eleui）分页查询", notes = "分页查询对应渠道角色记录，适配前端element-ui数据格式要求，提供展开功能所属数据而定义", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("eleui/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<RoleDto> eleuiTableList(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size) {
        return roleService.pageDto(new Page<>(current, size), channel, null);
    }

    @ApiOperation(value = "角色记录（适配eleui）分页检索", notes = "分页检索对应渠道角色记录，适配前端element-ui数据格式要求，提供展开功能所属数据而定义", httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @PostMapping("search/eleui/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<RoleDto> eleuiTableSearch(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size, @RequestBody Role role) {
        return roleService.pageDto(new Page<>(current, size), channel, role);
    }

    @ApiOperation(value = "角色记录查询", notes = "根据路径参数角色id查询其详细数据", httpMethod = "GET")
    @JsonView(View.DetailView.class)
    @GetMapping("{id:\\d+}")
    public RoleDto getRoleAndRelationRecords(@PathVariable Long id) {
        return roleService.getRoleAndRelationRecords(id);
    }

    /**
     * 是否关联选择资源的父级资源
     *
     * @param role
     * @return
     */
    private void _parseResourceIds(@NonNull Role role) {
        val newResList = new HashSet<Resource>();
        role.getResources().stream()
                .filter(item -> !item.getId().equals(Resource.IS_ROOT_MENU))
                .forEach(resource -> {
                    val pidsStr = resource.getPids();
                    if (StringUtils.isNotEmpty(pidsStr)) {
                        val pids = pidsStr.split(",");
                        for (val pid : pids) {
                            // 非根节点
                            if (NumberUtils.isCreatable(pid) && !pid.equals(String.valueOf(Resource.IS_ROOT_MENU))) {
                                val temp = Long.valueOf(pid);
                                val res = new Resource().setId(temp);
                                newResList.add((Resource) res);
                            }
                        }
                    }
                    newResList.add(resource);
                });
        List<Resource> list = new ArrayList<>(newResList.size());
        list.addAll(newResList);
        role.setResources(list);
    }

    /**
     * @param role 其中可能包含{@link Role#getResources()}，因为前端控件的原因，下面将会添加资源对应的父节点到需要关联的记录中
     * @return
     */
    @ApiOperation(value = "新增角色", notes = "添加角色和其管理的资源记录", httpMethod = "POST")
    @PostMapping
    @JsonView(View.DetailView.class)
    public Role create(@RequestBody @Validated({Default.class}) Role role) {
        roleService.saveSelfAndRelationRecords(role);
        return role;
    }

    @ApiOperation(value = "更新角色信息", notes = "更新角色信息和其管理的资源记录", httpMethod = "PUT")
    @PutMapping
    @JsonView(View.DetailView.class)
    public Role update(@RequestBody @Validated({BaseEntity.IDGroup.class, Default.class}) Role role) {
        _parseResourceIds(role);
        roleService.updateSelfAndRelationRecords(role);
        return role;
    }

    @ApiOperation(value = "批量删除角色", notes = "批量删除角色角色和其管理的资源记录", httpMethod = "DELETE")
    @DeleteMapping
    public Boolean dels(@RequestBody @Validated({Role.Groups.RoleDels.class}) Admin data) {
        val roles = data.getRoles();
        if (roles.size() > 0) {
            val idList = new HashSet<Long>();
            roles.forEach(item -> idList.add(item.getId()));
            return roleService.remove(idList);
        } else {
            throw new IllegalArgumentException("未传递要删除的角色记录");
        }
    }

}
