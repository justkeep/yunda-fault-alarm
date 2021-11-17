package com.yunda.faultalarm.biz.exception;

import lombok.Data;

/**
 * @author yuke.gong
 * @version : BizException.java, v 0.1 2021年11月16日 15:45 yuke.gong Exp $
 */
@Data
public class BizException extends RuntimeException{

    private static final long serialVersionUID = 8234590379190546142L;

    private int code;
    private String message;

    public BizException(int code, String message){
        this.code = code;
        this.message = message;
    }

    public BizException(String message){
        this.message = message;
    }
}