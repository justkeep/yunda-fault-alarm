package com.yunda.faultalarm.biz.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SaveCategoryPhoneConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 线路
     */
    @NotBlank(message = "线路名称不能为空")
    private String lineName;

    /**
     * 线路编码
     */
    @NotBlank(message = "线路编码不能为空")
    private String lineCode;

    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    private String phones;

    /**
     * 是否发送短信标志:1：发送 0 ：不发送
     */
    @NotNull(message = "是否发送短信标志不能为空")
    private Integer pushMsgFlag;

    /**
     * 同一条短信往同一个手机号一天内发送的次数
     */
    @NotNull
    private Integer frequency;

    /**
     * 每次发送时间间隔，单位小时
     */
    @NotNull
    private Integer cutOff;

    /**
     * 等级编码，多个以英文","分隔
     */
    @NotBlank(message = "等级必选")
    private String grade;

    /**
     * 部件：多个以英文","分隔
     */

    private String component;

}
