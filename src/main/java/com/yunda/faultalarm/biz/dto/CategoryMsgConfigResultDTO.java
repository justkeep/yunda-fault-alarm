package com.yunda.faultalarm.biz.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryMsgConfigResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalCount;
    private Long pageNum;
    private Long pageSize;
    private List<CategoryMsgConfigDetail> data;

    @Data
    public static class CategoryMsgConfigDetail{
        private Integer id;

        /**
         * 线路
         */
        private String lineName;

        /**
         * 线路编码
         */
        private String lineCode;

        /**
         * 电话号码
         */
        private String phones;

        /**
         * 是否发送短信标志:发送 不发送
         */
        private String pushMsgFlag;

        /**
         * 同一条短信往同一个手机号一天内发送的次数
         */
        private Integer frequency;

        /**
         * 每次发送时间间隔，单位小时
         */
        private Integer cutOff;

        /**
         * 故障等级
         */
        private List<GradeAndComponentResultDTO.Detail> grade;

        /**
         * 部件
         */
        private List<GradeAndComponentResultDTO.Detail> component;
    }

}
