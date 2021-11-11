package com.yunda.faultalarm.biz.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author www59
 */
@Data
public class GradeAndComponentResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Detail> grades;

    private List<Detail> components;

    @Data
    public static class Detail{
        /**
         * 属性编码
         */
        private String codeValue;

        /**
         * 展示名称
         */
        private String showName;

        /**
         * 组号
         */
        private String groupNumber;
    }

}
