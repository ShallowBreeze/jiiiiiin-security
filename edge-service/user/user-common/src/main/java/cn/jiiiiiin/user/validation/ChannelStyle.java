package cn.jiiiiiin.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * https://www.baeldung.com/spring-mvc-custom-validator
 */
@Documented
@Constraint(validatedBy = { ChannelStyleValidator.class })
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD })
public @interface ChannelStyle {

    String message() default "渠道参数不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
