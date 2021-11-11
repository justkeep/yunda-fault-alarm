package com.yunda.faultalarm.biz.dto;

import lombok.Data;

/**
 * @author www59
 */
@Data
public class QueryMsgParams {
    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 当前页
     */
    private Integer pageNum;

}
