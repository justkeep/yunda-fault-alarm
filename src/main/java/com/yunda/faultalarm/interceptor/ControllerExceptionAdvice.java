package com.yunda.faultalarm.interceptor;

import com.yunda.faultalarm.biz.dto.YundaFaultResponse;
import com.yunda.faultalarm.biz.exception.BizException;
import com.yunda.faultalarm.enums.ResultCode;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author yuke.gong
 * @version : ControllerExceptionAdvice.java, v 0.1 2021年11月16日 15:34 yuke.gong Exp $
 */
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public YundaFaultResponse MethodArgumentNotValidExceptionHandler(Exception e) {
        // 从异常对象中拿到ObjectError对象
        if (e instanceof BindException){
            BindException e1 = (BindException) e;
            ObjectError objectError = e1.getBindingResult().getAllErrors().get(0);
            return new YundaFaultResponse(ResultCode.PARAMS_ERROR.getValue(), objectError.getDefaultMessage());
        }else if (e instanceof BizException){
            BizException e1 = (BizException) e;
            return new YundaFaultResponse(e1.getCode(), e1.getMessage());
        }else {
            return new YundaFaultResponse(10005, "未知异常");
        }
    }
}