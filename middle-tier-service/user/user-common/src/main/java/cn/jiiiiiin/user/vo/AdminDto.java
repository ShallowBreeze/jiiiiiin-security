package cn.jiiiiiin.user.vo;

import cn.jiiiiiin.mvc.common.utils.View;
import cn.jiiiiiin.mvc.common.validation.Groups;
import cn.jiiiiiin.user.entity.Admin;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author jiiiiiin
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AdminDto extends Admin {

    private static final long serialVersionUID = -8768149378834759936L;
    @JsonView(View.SimpleView.class)
    private String createTimeStr;

    @JsonView(View.DetailView.class)
    @NotNull
    @Size(min = 1, groups = {Groups.Create.class})
    private String[] roleIds;

}
