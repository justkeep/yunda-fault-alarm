package com.yunda.faultalarm.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 故障信息
 * @author gyk
 */
@Data
public class YunDaFaultMessageDTO {

    @NotBlank(message = "cityCode不能为空")
    private String cityCode;

    @NotBlank(message = "line不能为空")
    private String line;

    @NotBlank(message = "fault不能为空")
    private String fault;
}
