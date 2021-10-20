package com.yunda.faultalarm.biz.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 故障所属分类信息
 * @author gyk
 */
@Data
public class FaultClassificationData {
    @ExcelProperty("城市行政编码")
    private String cityCode;
    @ExcelProperty("线路")
    private String line;
    @ExcelProperty("故障描述")
    private String fault;
    @ExcelProperty("所属分类")
    private String category;
}
