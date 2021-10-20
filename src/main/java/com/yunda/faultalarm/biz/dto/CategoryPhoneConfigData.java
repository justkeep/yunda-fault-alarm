package com.yunda.faultalarm.biz.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 分类配置的电话信息
 *
 * @author gyk
 */
@Data
public class CategoryPhoneConfigData {
    @ExcelProperty("城市行政编码")
    private String cityCode;

    @ExcelProperty("线路")
    private String line;

    @ExcelProperty("分类名称")
    private String category;

    @ExcelProperty("电话号码")
    private String phone;

    @ExcelProperty("是否发送短信")
    private String pushMsgFlag;
}
