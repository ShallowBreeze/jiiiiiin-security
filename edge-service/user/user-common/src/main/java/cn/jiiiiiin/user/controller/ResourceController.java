package cn.jiiiiiin.user.controller;


import cn.jiiiiiin.data.entity.BaseEntity;
import cn.jiiiiiin.data.util.View;
import cn.jiiiiiin.user.dto.ResourceDto;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.enums.StatusEnum;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.service.IResourceService;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限资源表 前端控制器
 * </p>
 *
 * TODO 增加校验器
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Slf4j
@RestController
@RequestMapping("/resource")
@Api
public class ResourceController extends BaseController {

    @Autowired
    private IResourceService resourceService;

    @ApiOperation(value = "检索获取资源树", notes = "获取对应渠道，对应状态的树形资源列表",httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("search/{channel:[0]}/{status:[01]}")
    public R<List<Resource>> searchTree(@PathVariable ChannelEnum channel, @PathVariable StatusEnum status) {
        // 前端需要一个【根节点】才好于进行前端逻辑控制
        val tree = new ArrayList<Resource>();
        tree.add(Resource
                .getRootMenu(channel)
                .setChildren(resourceService.searchTreeAllChildrenNode(Resource.IS_ROOT_MENU, channel, status))
        );
        return success(tree);
    }

    @ApiOperation(value = "查询资源树", notes = "查询对应渠道的树形资源列表",httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("{channel:[0]}")
    public R<List<Resource>> rootTree(@PathVariable ChannelEnum channel) {
        // 前端需要一个【根节点】才好于进行前端逻辑控制
        val tree = new ArrayList<Resource>();
        tree.add(Resource
                .getRootMenu(channel)
                .setChildren(resourceService.treeAllChildrenNode(Resource.IS_ROOT_MENU, channel))
        );
        return success(tree);
    }

    @ApiOperation(value = "查询对应节点下的资源树", notes = "查询对应节点下（根据路径参数资源id）的资源树",httpMethod = "GET")
    @JsonView(View.SimpleView.class)
    @GetMapping("{channel:[0]}/{id}")
    public R<List<Resource>> qryTree(@PathVariable ChannelEnum channel, @PathVariable Long id) {
        return success(resourceService.treeAllChildrenNode(id, channel));
    }

    @ApiOperation(value="查询资源和其关联接口记录", notes = "查询资源（根据路径参数资源id）和其关联接口记录",httpMethod = "GET")
    @JsonView(View.DetailView.class)
    @GetMapping("qry/{id:\\d+}")
    public R<ResourceDto> qry(@PathVariable Long id) {
        return success(resourceService.getResourceAndRelationRecords(id));
    }

    @ApiOperation(value = "新增资源", notes = "新增资源和关联的接口记录", httpMethod = "POST")
    @JsonView(View.DetailView.class)
    @PostMapping
    public R<Resource> create(@RequestBody @Validated({Default.class}) ResourceDto resource) {
        resourceService.saveAndSortNumAndRelationInterfaceRecords(resource);
        // 方便vue响应式数据属性定义
        return success(resource.setChildren(new ArrayList<>()));
    }

    @ApiOperation(value = "更新资源信息", notes = "更新资源和关联的接口记录", httpMethod = "PUT")
    @PutMapping
    public R<Resource> update(@RequestBody @Validated({BaseEntity.IDGroup.class, Default.class}) ResourceDto resource) {
        resourceService.updateAndSortNumAndRelationInterfaceRecords(resource);
        return success(resource);
    }

    @ApiOperation(value="删除资源记录", notes = "自有对应记录是叶子节点才允许删除", httpMethod = "DELETE")
    @DeleteMapping("{channel:[0]}/{id:\\d+}")
    public R<Boolean> del(@PathVariable ChannelEnum channel, @PathVariable Long id) {
        return success(resourceService.delOnlyIsLeafNode(id, channel));
    }

}
