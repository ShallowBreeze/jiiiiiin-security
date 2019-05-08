package cn.jiiiiiin.module.common.advice;

import cn.jiiiiiin.module.common.exception.BusinessErrException;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 统一异常处理
 *
 * @author jiiiiiin
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = BusinessErrException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<String> handlerBusinessErrException(HttpServletRequest req, BusinessErrException ex) {
        val apiResult = new R<String>();
        apiResult.setCode(ex.getCode());
        apiResult.setMsg(ex.getMessage());
        return apiResult;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<List<FieldError>> handlerMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder("参数传递错误：");
        bindingResult.getFieldErrors().forEach(fieldError -> {
            log.error("参数传递错误 {} {} {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            stringBuilder.append("[").append(fieldError.getField()).append("]").append(fieldError.getDefaultMessage()).append("<br/>");
        });
        return restResult(bindingResult.getFieldErrors(), ApiErrorCode.FAILED.getCode(), "参数传递错误");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Exception> handlerException(HttpServletRequest req, Exception ex) {
        return _commonHandler(ex);
    }

    private R<Exception> _commonHandler(Exception ex) {
        log.error("全局异常处理捕获到的错误", ex);
        R<Exception> response = R.failed(ex.getMessage());
        response.setData(ex);
        return response;
    }

    public static <T> R<T> restResult(T data, long code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

}
