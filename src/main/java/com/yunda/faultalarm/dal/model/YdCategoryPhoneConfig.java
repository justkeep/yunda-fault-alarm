package com.yunda.faultalarm.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("yd_category_phone_config")
public class YdCategoryPhoneConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 线路
     */
    private String lineName;

    /**
     * 线路编码
     */
    private String lineCode;

    /**
     * 电话号码
     */
    private String phones;

    /**
     * 是否发送短信标志:1：发送 0 ：不发送
     */
    private Integer pushMsgFlag;

    /**
     * 同一条短信往同一个手机号一天内发送的次数
     */
    private Integer frequency;

    /**
     * 每次发送时间间隔，单位小时
     */
    private Integer cutOff;

    private String grade;

    private String component;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    private Integer delFlag;


}
