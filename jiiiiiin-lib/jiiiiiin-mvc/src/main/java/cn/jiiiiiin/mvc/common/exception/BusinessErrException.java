package cn.jiiiiiin.mvc.common.exception;

import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import lombok.Getter;

/**
 * 不同的模块使用自己的异常类，并基础当前类
 * @author jiiiiiin
 */
@Getter
public abstract class BusinessErrException extends RuntimeException {

    private static final long serialVersionUID = -2980801340970539947L;
    private long code = ApiErrorCode.FAILED.getCode();

    public BusinessErrException(String message) {
        super(message);
    }

    public BusinessErrException(String message, long code) {
        super(message);
        this.code = code;
    }
}
