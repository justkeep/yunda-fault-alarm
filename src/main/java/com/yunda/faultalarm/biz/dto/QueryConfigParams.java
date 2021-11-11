package com.yunda.faultalarm.biz.dto;

import lombok.Data;

/**
 * @author www59
 */
@Data
public class QueryConfigParams {
    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 当前页
     */
    private Integer pageNum;

}
