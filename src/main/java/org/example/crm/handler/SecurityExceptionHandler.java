package org.example.crm.handler;

import org.example.crm.result.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class SecurityExceptionHandler {
    // 处理权限不足异常
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public R handleAccessDenied() {
        return R.builder()
                .code(403)
                .msg("无权限")
                .info("无权限，请订阅专业版")
                .build();
    }
}
