package com.yunda.faultalarm.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yuke.gong
 * @version : YdWangyiConfig.java, v 0.1 2,021年11月27日 20:9 yuke.gong Exp $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_wangyi_config")
public class YdWangyiConfig extends Model<YdWangyiConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String appKey;

    private String appSecret;

    /**
     * 模板Id
     */
    private String templateId;

    /**
     * 模板变量数量
     */
    private Integer paramNumber;

    private String requestUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
