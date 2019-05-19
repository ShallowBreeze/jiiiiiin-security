package cn.jiiiiiin.user.vo;

import cn.jiiiiiin.user.entity.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author jiiiiiin
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ResourceVO extends Resource {

    private static final long serialVersionUID = -1071063583966714325L;
    private Long[] interfacesIds;

}
