package cn.jiiiiiin.module.mngauth.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.commons.lang3.math.NumberUtils;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;

/**
 * <p>
 * 通用枚举注入演示，注意需要实现 IEnums 也需要扫描枚举包
 * </p>
 *
 * @author hubin
 * @since 2018-08-15
 */
public enum StatusEnum implements IEnum<Integer> {
    STOP(0, "停用"),
    ENABLE(1, "启用");

    private int value;
    private String desc;

    StatusEnum(final int value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public static StatusEnum fromValue(Integer value) {
        for (StatusEnum item : values()) {
            if (item.value == value) {
                return item;
            }
        }
        throw new IllegalArgumentException(
                "待查找的状态 [" + value + "]不存在, 当前所支持的状态标识： " + Arrays.toString(values()));
    }

    public static class ResourceChannelEnumConverter extends PropertyEditorSupport {
        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            if (NumberUtils.isCreatable(text)) {
                setValue(StatusEnum.fromValue(Integer.valueOf(text)));
            } else {
                throw new IllegalArgumentException(String.format("待转换的渠道参数%s不是正确的数值类型", text));
            }
        }
    }
}
