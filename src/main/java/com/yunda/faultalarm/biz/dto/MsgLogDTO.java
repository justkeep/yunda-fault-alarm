package com.yunda.faultalarm.biz.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class MsgLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalCount;
    private Long pageNum;
    private Long pageSize;
    private List<MsgDetail> data;

    @Data
    public static class MsgDetail{
        private Long id;
        /**
         * 线路名称
         */
        private String lineName;

        /**
         * 线路编码
         */
        private String lineCode;

        /**
         * 短信内容
         */
        private String content;

        /**
         * 电话
         */
        private String phone;

        /**
         * 短信发送时间
         */
        private String sendTime;

        /**
         * 成功，失败
         */
        private String sendStatus;

        private String reason;
    }

}
