package cn.jiiiiiin.user.controller;


import cn.jiiiiiin.user.entity.Interface;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.exception.UserServiceException;
import cn.jiiiiiin.user.service.IInterfaceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统接口表 前端控制器
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-12-09
 */
@Api(tags = "mngauth", description = "系统接口表 前端控制器")
@RestController
@RequestMapping("/interface")
public class InterfaceController extends BaseController {

    @Autowired
    private IInterfaceService interfaceService;

    @ApiOperation(value = "接口记录列表查询", httpMethod = "GET")
    @GetMapping("list/{channel:[0]}")
    public List<Interface> list(@PathVariable ChannelEnum channel) {
        return interfaceService.list(new QueryWrapper<Interface>().eq(Interface.CHANNEL, channel));
    }

    @ApiOperation(value = "接口记录分页查询", httpMethod = "GET")
    @GetMapping("{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<Interface> list(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size) {
        return interfaceService.page(new Page<>(current, size), new QueryWrapper<Interface>().eq(Interface.CHANNEL, channel));
    }

    @ApiOperation(value = "接口记录分页检索", httpMethod = "GET")
    @PostMapping("search/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public IPage<Interface> search(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size, @RequestBody Interface itf) {
        val qw = new QueryWrapper<Interface>()
                .eq(Interface.CHANNEL, channel);
        if (StringUtils.isNotEmpty(itf.getName())) {
            qw.like(Interface.NAME, itf.getName());
        }
        if (StringUtils.isNotEmpty(itf.getUrl())) {
            qw.like(Interface.URL, itf.getUrl());
        }
        if (StringUtils.isNotEmpty(itf.getMethod())) {
            qw.eq(Interface.METHOD, itf.getMethod());
        }
        if (itf.getStatus() != null) {
            qw.eq(Interface.STATUS, itf.getStatus());
        }
        return interfaceService.page(new Page<>(current, size), qw);
    }

    @ApiOperation(value = "通过id查询接口记录", httpMethod = "GET")
    @GetMapping("{id:\\d+}")
    public Interface getAdminAndRelationRecords(@PathVariable Long id) {
        return interfaceService.getById(id);
    }

    @ApiOperation(value ="新增接口记录", httpMethod = "POST")
    @PostMapping
    public Interface create(@RequestBody @Validated({Default.class}) Interface itf) {
        if (interfaceService.saveOrUpdate(itf)) {
            return itf;
        } else {
            throw new UserServiceException("新增接口记录失败");
        }
    }

    @ApiOperation(value="更新接口记录", httpMethod = "PUT")
    @PutMapping
    public Interface update(@RequestBody @Validated({Default.class}) Interface itf) {
        if (interfaceService.saveOrUpdate(itf)) {
            return itf;
        } else {
            throw new UserServiceException("修改接口失败");
        }
    }

    @ApiOperation(value="批量删除接口记录", httpMethod = "DELETE")
    @DeleteMapping("dels/{ids:^[\\d,]+$}")
    public Boolean dels(@PathVariable String ids) {
        return interfaceService.removeByIds(Arrays.asList(ids.split(",")));
    }


}
