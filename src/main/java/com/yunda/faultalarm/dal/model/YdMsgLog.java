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
 * 短信发送日志记录表
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_msg_log")
public class YdMsgLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 线路编码
     */
    private String lineCode;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 电话
     */
    private String phone;

    /**
     * 短信发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 发送成功:success,发送失败：fail，不发送：inexecution
     */
    private String sendStatus;

    /**
     * 报警时间
     */
    private String alarmTime;

    /**
     * 列车号
     */
    private String numTrain;

    /**
     * 列车号别名
     */
    private String aliasTrain;

    /**
     * 车辆号
     */
    private String numVehicle;

    /**
     * 车辆号别名
     */
    private String aliasVehicle;

    /**
     * 项目
     */
    private String category;

    /**
     * 分类
     */
    private String subCategory;

    /**
     * 等级
     */
    private String alarmGrade;

    /**
     * 部件
     */
    private String component;

    /**
     * 子部件
     */
    private String subpart;

    /**
     * 子部件别名
     */
    private String aliasSubpart;

    /**
     * 架号
     */
    private String numBogie;

    /**
     * 轴号
     */
    private String numAxle;

    /**
     * 测点号
     */
    private String numLocation;

    /**
     * 测点号别名
     */
    private String aliasLocation;

    /**
     * 前置处理器号
     */
    private String codeQz;

    private String extInfo;

    /**
     * 原因
     */
    private String reason;

    /**
     * 配置Id
     */
    private Integer configId;

}
