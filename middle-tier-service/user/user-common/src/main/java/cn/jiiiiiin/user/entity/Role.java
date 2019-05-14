package cn.jiiiiiin.user.entity;

import cn.jiiiiiin.data.entity.BaseEntity;
import cn.jiiiiiin.user.common.exception.BusinessErrException;
import cn.jiiiiiin.user.common.utils.View;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.validation.ChannelStyle;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-09-27
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mng_role")
@ApiModel(value = "Role对象", description = "角色表")
public class Role extends BaseEntity<Role> {

    public static class Groups {

        public interface RoleDels {
        }

    }

    public static final Long ROLE_ADMIN_ID = 1061277220292595713L;
    private static final long serialVersionUID = 7045256792828824071L;

    @SuppressWarnings("AlibabaAvoidComplexCondition")
    public static void checkRootRole(@NonNull Role role, @NonNull String errMsg){
        if((role.getId() != null && role.getId().equals(ROLE_ADMIN_ID))
                || "Admin".equals(role.getAuthorityName())
                || "系统管理员".equals(role.getName())) {
            throw new BusinessErrException(errMsg);
        }
    }

    public static void checkRootRole(@NonNull Collection<? extends Serializable> idList, @NonNull String errMsg){
        if(idList.stream().anyMatch(p -> p.equals(ROLE_ADMIN_ID))) {
            throw new BusinessErrException(errMsg);
        }
    }

    @ApiModelProperty(value = "角色名称")
    @JsonView(View.SimpleView.class)
    @NotBlank(message = "角色名称不能为空")
    @Length(min = 2, max = 10, message = "角色名称长度必须在2~10位之间")
    private String name;

    @ApiModelProperty(value = "角色标识")
    @JsonView(View.SimpleView.class)
    @NotBlank(message = "角色标识不能为空")
    @Length(min = 2, max = 10, message = "角色标识长度必须在2~10位之间")
    private String authorityName;

    @ApiModelProperty(value = "标识渠道，不同的渠道就是不同的资源分组: 0:内管")
    @JsonView(View.DetailView.class)
    @ChannelStyle
    private ChannelEnum channel;

    @TableField(exist = false)
    @JsonView(View.DetailView.class)
    private List<Resource> resources = new LinkedList<>();

    public static final String NAME = "name";

    public static final String AUTHORITY_NAME = "authority_name";

    public static final String CHANNEL = "channel";

}
