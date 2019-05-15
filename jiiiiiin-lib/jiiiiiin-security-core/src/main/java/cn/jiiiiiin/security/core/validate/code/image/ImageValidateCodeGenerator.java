package cn.jiiiiiin.security.core.validate.code.image;

import cn.jiiiiiin.security.core.dict.SecurityConstants;
import cn.jiiiiiin.security.core.properties.SecurityProperties;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeGenerator;
import com.google.code.kaptcha.Producer;
import com.wf.captcha.Captcha;
import com.wf.captcha.ChineseGifCaptcha;
import com.wf.captcha.GifCaptcha;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.awt.image.BufferedImage;

import static cn.jiiiiiin.security.core.properties.ImageCodeProperties.Type.EASYCAPTCHA_GIF_CHINESE;
import static cn.jiiiiiin.security.core.properties.ImageCodeProperties.Type.EASYCAPTCHA_GIF_TYPE_NUM_AND_UPPER;

/**
 * 默认的图形验证码生成器
 *
 * @author jiiiiiin
 */
@Slf4j
public class ImageValidateCodeGenerator implements ValidateCodeGenerator {

    private final SecurityProperties securityProperties;

    private Producer captchaProducer;

    public ImageValidateCodeGenerator(SecurityProperties securityProperties, Producer captchaProducer) {
        this.securityProperties = securityProperties;
        this.captchaProducer = captchaProducer;
    }

    public ImageValidateCodeGenerator(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public ImageCode generate(ServletWebRequest request) {
        ImageCode imageCode;
        val expireIn = ServletRequestUtils.getIntParameter(request.getRequest(), SecurityConstants.DEFAULT_PARAMETER_NAME_EXPIRE_IN, securityProperties.getValidate().getImageCode().getExpireIn());
        if (captchaProducer != null) {
            // 生成验证码
            final String capText = captchaProducer.createText();
            final BufferedImage bi = captchaProducer.createImage(capText);
            imageCode = new ImageCode(capText, bi, expireIn);
            log.info("图形验证码：【 {} 】 有效期： 【 {} 】秒", capText, expireIn);
        } else {
            var width = ServletRequestUtils.getIntParameter(request.getRequest(), "width", Integer.parseInt(securityProperties.getValidate().getImageCode().getWidth()));
            var height = ServletRequestUtils.getIntParameter(request.getRequest(), "height", Integer.parseInt(securityProperties.getValidate().getImageCode().getHeight()));
            var length = Integer.parseInt(securityProperties.getValidate().getImageCode().getLength());
            Captcha captcha;
            val type = securityProperties.getValidate().getImageCode().getType();
            if (type.equals(EASYCAPTCHA_GIF_CHINESE.name())) {
                captcha = new ChineseGifCaptcha(width, height, length);
            } else if (type.equals(EASYCAPTCHA_GIF_TYPE_NUM_AND_UPPER.name())) {
                captcha = new GifCaptcha(width, height, length);
            } else {
                captcha = new ChineseGifCaptcha(width, height, length);
            }
            // 生成验证码
            final String capText = captcha.text();
            imageCode = new ImageCode(capText, captcha, expireIn);
            log.info("图形验证码：【 {} 】 有效期： 【 {} 】秒", capText, expireIn);
        }
        return imageCode;
    }
}
