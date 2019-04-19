package cn.jiiiiiin.security.core.validate.code.sms;

import cn.jiiiiiin.security.core.validate.code.ValidateCodeController;
import cn.jiiiiiin.security.core.validate.code.sms.SmsCodeSender;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认实现
 *
 * @author jiiiiiin
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {

    @Override
    public void send(String mobilePhone, String validateCode) {
        log.debug("向用户手机 {} 发送验证码 {}", mobilePhone, validateCode);
    }
}
