package cn.jiiiiiin.module.mngauth.entity;

import cn.jiiiiiin.data.orm.entity.BaseEntity;
import cn.jiiiiiin.data.orm.util.View;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import cn.jiiiiiin.module.mngauth.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * <p>
 * 系统接口表
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-12-09
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@TableName("mng_interface")
@ApiModel(value="Interface对象", description="系统接口表")
public class Interface extends BaseEntity<Interface> {

    private static final long serialVersionUID = 7376983565924190370L;

    @ApiModelProperty(value = "接口名称")
    @TableField("name")
    @JsonView(View.SimpleView.class)
    @NotBlank(message = "接口名称不能为空")
    @Length(min = 2, max = 20, message = "接口名称长度必须在2~20位之间")
    private String name;

    @ApiModelProperty(value = "接口地址")
    @TableField("url")
    @JsonView(View.SimpleView.class)
    private String url;

    @ApiModelProperty(value = "接口类型，如GET标识添加")
    @TableField("method")
    @JsonView(View.SimpleView.class)
    private String method;

    @ApiModelProperty(value = "接口描述")
    @TableField("description")
    @JsonView(View.SimpleView.class)
    private String description;

    @ApiModelProperty(value = "接口状态:  1:启用   0:不启用")
    @TableField("status")
    @JsonView(View.SimpleView.class)
    private StatusEnum status;

    @ApiModelProperty(value = "标识渠道，不同的渠道就是不同的资源分组: 0:内管")
    @TableField("channel")
    private ChannelEnum channel;


    public static final String NAME = "name";

    public static final String URL = "url";

    public static final String METHOD = "method";

    public static final String DESCRIPTION = "description";

    public static final String STATUS = "status";

    public static final String CHANNEL = "channel";

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interface)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Interface that = (Interface) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(method, that.method) &&
                channel == that.channel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), url, method, channel);
    }
}
