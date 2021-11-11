package com.yunda.faultalarm.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 故障等级配置表
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_fault_grade_config")
public class YdFaultGradeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 属性编码
     */
    private String codeValue;

    /**
     * 属性描述
     */
    private String description;

    /**
     * 展示名称
     */
    private String showName;

    /**
     * 类别:	            等级：grade，部件：component
     */
    private String category;

    /**
     * 组号
     */
    private String groupNumber;


}
