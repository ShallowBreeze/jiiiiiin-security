package cn.jiiiiiin.security.core.validate.code.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
@NoArgsConstructor
public class ValidateCode implements Serializable {

    private static final long serialVersionUID = 3611750510059703824L;

    /**
     * 验证码
     */
    private String code;

    /**
     * 到期时间 单位秒
     */
    private int originExpireSecondsTime;

    /**
     * https://blog.csdn.net/u011035407/article/details/77191115
     * 到期时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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
     * <p>
     * 注：这里不能直接定义为属性，否则会出现`com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: `
     * https://www.baeldung.com/jackson-deserialize-json-unknown-properties
     *
     * @return 返回true标明已经过期
     */
    public static boolean isExpired(LocalDateTime expireTime) {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
