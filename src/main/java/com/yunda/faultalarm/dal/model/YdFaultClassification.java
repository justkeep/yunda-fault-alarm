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
 * 故障分类信息表
 * </p>
 *
 * @author GYK
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_fault_classification")
public class YdFaultClassification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 线路名称
     */
    private String line;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 故障描述
     */
    private String faultDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    private LocalDateTime updatedAt;


}
