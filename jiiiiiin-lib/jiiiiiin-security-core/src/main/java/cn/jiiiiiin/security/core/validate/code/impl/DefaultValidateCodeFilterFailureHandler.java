package cn.jiiiiiin.security.core.validate.code.impl;

import cn.jiiiiiin.security.core.dict.CommonConstants;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeException;
import cn.jiiiiiin.security.core.validate.code.ValidateCodeFilterFailureHandler;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class DefaultValidateCodeFilterFailureHandler implements ValidateCodeFilterFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onValidateFailure(HttpServletRequest request, HttpServletResponse response, ValidateCodeException exception) throws IOException {
        log.info("验证码校验失败");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(CommonConstants.CONTENT_TYPE_JSON);
        response.getWriter().write(objectMapper.writeValueAsString(R.failed(exception.getMessage())));
    }
}
