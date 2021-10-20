package com.yunda.faultalarm.biz.service;

/**
 * 故障处理
 * @author gyk
 */
public interface IFaultService {

    /**
     *
     * @param cityCode  -- 城市编码
     * @param line -- 线路
     * @param fault -- 故障码
     * @return
     */
    boolean processFault(String cityCode,String line,String fault);
}
