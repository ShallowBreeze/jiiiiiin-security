package cn.jiiiiiin.user.entity;

import cn.jiiiiiin.data.entity.BaseEntity;
import cn.jiiiiiin.data.util.View;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.enums.StatusEnum;
import cn.jiiiiiin.user.enums.ResourceTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * <p>
 * 权限资源表
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@TableName("mng_resource")
@ApiModel(value = "Resource对象", description = "权限资源表")
public class Resource extends BaseEntity<Resource> {

    /**
     * 根节点
     */
    public static final Long IS_ROOT_MENU = 0L;
    private static final long serialVersionUID = -4096660792214425988L;

    public static Resource getRootMenu(ChannelEnum channel) {
        return (Resource) new Resource()
                .setName("根节点")
                .setStatus(StatusEnum.ENABLE)
                .setChannel(channel)
                .setId(Resource.IS_ROOT_MENU);
    }

    @ApiModelProperty(value = "资源父id: 0标识(默认)为根节点")
    @JsonView(View.SimpleView.class)
    private Long pid;

    @ApiModelProperty(value = "当前资源的所有父节点id集合: 0(默认)")
    @JsonView(View.SimpleView.class)
    private String pids;

    @ApiModelProperty(value = "资源名称")
    @JsonView(View.SimpleView.class)
    @NotBlank(message = "资源名称不能为空")
    @Length(min = 2, max = 10, message = "资源名称长度必须在2~10位之间")
    private String name;

    @ApiModelProperty(value = "资源别名，用于唯一标识当前资源，方便前端进行权限控制，生成规则建议使用：渠道标识+大写英文")
    @JsonView(View.SimpleView.class)
    private String alias;

    @ApiModelProperty(value = "资源图标")
    @JsonView(View.SimpleView.class)
    private String icon;

    @ApiModelProperty(value = "资源前端路由地址")
    @JsonView(View.SimpleView.class)
    private String path;

    @ApiModelProperty(value = "资源排序号")
    @JsonView(View.SimpleView.class)
    private Integer num;

    @ApiModelProperty(value = "资源层级")
    @JsonView(View.SimpleView.class)
    private Integer levels;

    @ApiModelProperty(value = "资源类型: 1:菜单(默认) 0:按钮")
    @JsonView(View.SimpleView.class)
    private ResourceTypeEnum type;

    @ApiModelProperty(value = "资源状态: 1:启用(默认) 0:停用")
    @JsonView(View.SimpleView.class)
    private StatusEnum status;

    @ApiModelProperty(value = "渠道标识，不同的渠道就是不同的资源分组: 0:内管")
    @JsonView(View.DetailView.class)
    private ChannelEnum channel;

    @TableField(exist = false)
    private List<Resource> children;

    @TableField(exist = false)
    private Set<Interface> interfaces = new HashSet<>();

    public static final String PID = "pid";

    public static final String PIDS = "pids";

    public static final String NAME = "name";

    public static final String ALIAS = "alias";

    public static final String ICON = "icon";

    public static final String PATH = "path";

    public static final String NUM = "num";

    public static final String LEVELS = "levels";

    public static final String TYPE = "type";

    public static final String STATUS = "status";

    public static final String CHANNEL = "channel";

    /**
     * 递归解析一级节点下面的子节点
     *
     * @param resource
     * @param menuResources
     * @return
     */
    public static Resource parserMenu(Resource resource, Collection<Resource> menuResources) {
        val children = new ArrayList<Resource>();
        val pid = resource.getId();
        menuResources.forEach((item) -> {
            // 添加子节点
            if (item.getPid().equals(pid)) {
                // 递归出子元素
                val node = parserMenu(item, menuResources);
                children.add(node);
            }
        });
        if (children.size() > 0) {
            resource.setChildren(children);
            resource.getChildren().sort(Comparator.comparingInt(Resource::getNum));
        }
        return resource;
    }

}
