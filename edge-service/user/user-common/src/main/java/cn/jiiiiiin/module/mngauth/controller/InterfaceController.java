package cn.jiiiiiin.module.mngauth.controller;


import cn.jiiiiiin.module.mngauth.entity.Interface;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import cn.jiiiiiin.module.common.exception.BusinessErrException;
import cn.jiiiiiin.module.mngauth.service.IInterfaceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public R<List<Interface>> list(@PathVariable ChannelEnum channel) {
        return R.ok(interfaceService.list(new QueryWrapper<Interface>().eq(Interface.CHANNEL, channel)));
    }

    @ApiOperation(value = "接口记录分页查询", httpMethod = "GET")
    @GetMapping("{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public R<IPage<Interface>> list(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size) {
        return R.ok(interfaceService.page(new Page<>(current, size), new QueryWrapper<Interface>().eq(Interface.CHANNEL, channel)));
    }

    @ApiOperation(value = "接口记录分页检索", httpMethod = "GET")
    @PostMapping("search/{channel:[0]}/{current:\\d+}/{size:\\d+}")
    public R<IPage<Interface>> search(@PathVariable ChannelEnum channel, @PathVariable Long current, @PathVariable Long size, @RequestBody Interface itf) {
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
        return R.ok(interfaceService.page(new Page<>(current, size), qw));
    }

    @ApiOperation(value = "通过id查询接口记录", httpMethod = "GET")
    @GetMapping("{id:\\d+}")
    public R<Interface> getAdminAndRelationRecords(@PathVariable Long id) {
        return success(interfaceService.getById(id));
    }

    @ApiOperation(value ="新增接口记录", httpMethod = "POST")
    @PostMapping
    public R<Interface> create(@RequestBody @Validated({Default.class}) Interface itf) {
        if (interfaceService.saveOrUpdate(itf)) {
            return success(itf);
        } else {
            throw new BusinessErrException("新增接口记录失败");
        }
    }

    @ApiOperation(value="更新接口记录", httpMethod = "PUT")
    @PutMapping
    public R<Interface> update(@RequestBody @Validated({Default.class}) Interface itf) {
        if (interfaceService.saveOrUpdate(itf)) {
            return success(itf);
        } else {
            throw new BusinessErrException("修改接口失败");
        }
    }

    @ApiOperation(value="批量删除接口记录", httpMethod = "DELETE")
    @DeleteMapping("dels/{ids:^[\\d,]+$}")
    public R<Boolean> dels(@PathVariable String ids) {
        return success(interfaceService.removeByIds(Arrays.asList(ids.split(","))));
    }


}
