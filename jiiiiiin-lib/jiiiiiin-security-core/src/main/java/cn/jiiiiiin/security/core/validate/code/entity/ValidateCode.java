package cn.jiiiiiin.security.core.validate.code.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * `Caused by: org.springframework.core.serializer.support.SerializationFailedException: Failed to serialize object using DefaultSerializer; nested exception is java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [cn.jiiiiiin.security.core.validate.code.image.ImageCode]`
 * 开启 spring session -》 redis 之后，我们的 session 将会被存储到 redis 中，但是我们存放到 session 中的图形验证码没有实现`Serializable`接口，导致不可以被序列号到 redis 中，故报出此错误；
 *
 * @author jiiiiiin
 */
@Setter
@Getter
public class ValidateCode implements Serializable {

    private static final long serialVersionUID = 3611750510059703824L;

    /**
     * 验证码
     */
    private String code;

    /**
     * 到期时间 单位秒
     */
    private final int originExpireSecondsTime;

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;

    /**
     * @param code
     * @param expireIn 多少秒后过期
     */
    public ValidateCode(String code, int expireIn) {
        this.code = code;
        this.originExpireSecondsTime = expireIn;
        this.expireTime = LocalDateTime.now().plusSeconds(this.originExpireSecondsTime);
    }

    /**
     * 判断验证码是否过期
     *
     * @return 返回true标明已经过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
