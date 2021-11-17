package com.yunda.faultalarm.enums;

import lombok.Getter;

/**
 * @author yuke.gong
 * @version : ResultCode.java, v 0.1 2021年11月16日 15:35 yuke.gong Exp $
 */
@Getter
public enum ResultCode {

    CODE_QZ_ERROR(10001),
    ALIAS_LOCATION_ERROR(10002),
    NUM_BOGIE_ERROR(10003),
    PARAMS_ERROR(10004);

    private int value;

    ResultCode(int value){
        this.value = value;
    }
}