package cn.jiiiiiin.user.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.extern.slf4j.Slf4j;
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
public enum ChannelEnum implements IEnum<Integer> {

    /**
     * 标识内管应用`manager-app`
     */
    MNG(0, "内管");

    private int value;
    private String desc;

    ChannelEnum(final int value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public static ChannelEnum fromValue(Integer value) {
        for (ChannelEnum item : values()) {
            if (item.value == value) {
                return item;
            }
        }
        throw new IllegalArgumentException(
                "待查找的渠道 [" + value + "]不存在, 当前所支持的渠道标识： " + Arrays.toString(values()));
    }

    @Slf4j
    public static class ResourceChannelEnumConverter extends PropertyEditorSupport {
        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            log.debug("ResourceChannelEnumConverter#setAsText origin value: %s", text);
            if (NumberUtils.isCreatable(text)) {
                setValue(ChannelEnum.fromValue(Integer.valueOf(text)));
            } else if(text.equalsIgnoreCase(MNG.name())){
                // 注意：这里是为了适配feign的远程调用
                setValue(MNG);
            } else {
                throw new IllegalArgumentException(String.format("待转换的渠道参数%s不是正确的数值类型", text));
            }
        }
    }
}
