package cn.jiiiiiin.data.orm.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 基础父类测试
 * </p>
 *
 * @author hubin
 * @author jiiiiiin
 * @since 2018-08-11
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class BaseEntity<T extends Model> extends Model<T> {

    public interface IDGroup {
    }

    private static final long serialVersionUID = 7792309132836966596L;
    /**
     * 数据库表主键
     * <p>
     * http://mp.baomidou.com/guide/faq.html#id-worker-生成主键太长导致-js-精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "参数id不能为空", groups = {IDGroup.class})
    private Long id;

    @Override
    protected Serializable pkVal() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static final String ID = "id";
}
