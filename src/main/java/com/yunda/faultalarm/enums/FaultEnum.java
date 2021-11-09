package com.yunda.faultalarm.enums;

import lombok.Getter;

/**
 * @author gyk
 */
@Getter
public enum FaultEnum {
    MESSAGE_SEND("发送"),
    MESSAGE_NO_SEND("不发送"),
    SEND_SUCCESS("success"),
    SEND_FAIL("fail");

    private String value;

    FaultEnum(String value){
        this.value = value;
    }
}
