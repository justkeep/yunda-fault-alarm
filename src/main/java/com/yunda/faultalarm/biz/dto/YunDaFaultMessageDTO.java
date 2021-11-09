package com.yunda.faultalarm.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 故障信息
 * @author gyk
 */
@Data
public class YunDaFaultMessageDTO {

    @NotBlank(message = "time不能为空")
    private String time;

    @NotBlank(message = "line不能为空")
    private String line;

    @NotBlank(message = "fault不能为空")
    private String fault;
}
