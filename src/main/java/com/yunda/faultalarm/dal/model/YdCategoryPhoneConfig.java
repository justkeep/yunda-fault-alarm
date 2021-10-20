package com.yunda.faultalarm.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.sql.Blob;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 故障分类电话配置表
 * </p>
 *
 * @author GYK
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_category_phone_config")
public class YdCategoryPhoneConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 线路
     */
    private String line;

    /**
     * 分类
     */
    private String category;

    /**
     * 电话号码
     */
    private String phones;

    /**
     * 是否发送短信：发送 , 不发送
     */
    private String pushMsgFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
