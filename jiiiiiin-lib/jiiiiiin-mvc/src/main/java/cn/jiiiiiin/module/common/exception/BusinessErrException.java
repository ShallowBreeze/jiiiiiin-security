package cn.jiiiiiin.module.common.exception;

import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import lombok.Getter;

/**
 * @author jiiiiiin
 */
@Getter
public class BusinessErrException extends RuntimeException {

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
