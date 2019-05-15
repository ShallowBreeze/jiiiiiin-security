package cn.jiiiiiin.security.core.validate.code.image;

import cn.jiiiiiin.security.core.validate.code.entity.ValidateCode;
import com.wf.captcha.Captcha;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * @author jiiiiiin
 */
@Getter
@Setter
public class ImageCode extends ValidateCode {

    private static final long serialVersionUID = -5743733411744551242L;
    /**
     * 根据验证码生成的图片
     */
    private BufferedImage image;

    private Captcha captcha;

    /**
     * @param code
     * @param captcha 验证码生成器
     * @param expireTime
     */
    public ImageCode(String code, Captcha captcha, int expireTime) {
        super(code, expireTime);
        this.captcha = captcha;
    }

    /**
     * @param code
     * @param image
     * @param expireIn 多少秒后过期
     */
    public ImageCode(String code, BufferedImage image, int expireIn) {
        super(code, expireIn);
        this.image = image;
    }
}
