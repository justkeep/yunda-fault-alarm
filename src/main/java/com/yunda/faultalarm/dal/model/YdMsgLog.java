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
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_msg_log")
public class YdMsgLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String cityCode;

    /**
     * 线路
     */
    private String line;

    /**
     * 故障描述
     */
    private String fault;

    /**
     * 电话
     */
    private String phone;

    private LocalDateTime pushTime;


}
