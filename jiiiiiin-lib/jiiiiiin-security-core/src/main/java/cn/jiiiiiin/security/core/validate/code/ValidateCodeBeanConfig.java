package cn.jiiiiiin.security.core.validate.code;

import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.security.core.validate.code.impl.DefaultValidateCodeFilterFailureHandler;
import cn.jiiiiiin.security.core.validate.code.sms.DefaultSmsCodeSender;
import cn.jiiiiiin.security.core.validate.code.image.ImageValidateCodeGenerator;
import cn.jiiiiiin.security.core.validate.code.sms.SmsCodeSender;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.jiiiiiin.security.core.properties.ImageCodeProperties.Type.KAPTCHA;

/**
 * @author jiiiiiin
 */
@Configuration
public class ValidateCodeBeanConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private Producer captchaProducer;

    @Bean
    @ConditionalOnMissingClass
    public ValidateCodeFilterFailureHandler validateCodeFilterFailureHandler(){
        return new DefaultValidateCodeFilterFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "imageValidateCodeGenerator")
    public ValidateCodeGenerator imageValidateCodeGenerator() {
        ImageValidateCodeGenerator imageValidateCodeGenerator;

        if (securityProperties.getValidate().getImageCode().getType().equals(KAPTCHA.name())) {
            imageValidateCodeGenerator = new ImageValidateCodeGenerator(securityProperties, captchaProducer);
        }else{
            imageValidateCodeGenerator = new ImageValidateCodeGenerator(securityProperties);
        }

        return imageValidateCodeGenerator;
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsCodeSender")
    public SmsCodeSender smsCodeSender() {
        return new DefaultSmsCodeSender();
    }


}
