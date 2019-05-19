package cn.jiiiiiin.user.client.component.converter;

import cn.jiiiiiin.user.enums.ChannelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

/**
 * TODO 当前类没有能正确的转型
 * @author jiiiiiin
 */
@Slf4j
public class ChannelEnumConverter implements Converter<ChannelEnum, Integer> {

    @Override
    public Integer convert(ChannelEnum channelEnum) {
        log.debug("ChannelEnumConverter#convert执行 %s", channelEnum.getValue());
        return channelEnum.getValue();
    }

}
