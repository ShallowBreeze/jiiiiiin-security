package cn.jiiiiiin.user.vo;

import cn.jiiiiiin.user.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 适配前端`element-ui#table`展开选择属性
 * <p>
 * http://element.eleme.io/#/zh-CN/component/table
 *
 * @author jiiiiiin
 */
@Data
@Accessors
@EqualsAndHashCode(callSuper = true)
public class RoleVO extends Role {
    private static final long serialVersionUID = 4460132236362989044L;
    private String[] expandedKeys = new String[]{};
    private String[] checkedKeys = new String[]{};
}
