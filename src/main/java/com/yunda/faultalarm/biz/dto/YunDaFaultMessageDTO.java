package com.yunda.faultalarm.biz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 故障信息
 * @author gyk
 */
@Data
public class YunDaFaultMessageDTO implements Serializable {

    @NotBlank(message = "报警时间不能为空")
    @JsonProperty(value = "Alarm_Time")
    private String alarmTime;

    @JsonProperty("Num_line")
    @NotBlank(message = "线路编码不能为空")
    private String lineCode;

    @JsonProperty("Num_Train")
    @NotBlank(message = "列车号不能为空")
    private String numTrain;

    @JsonProperty("Alias_Train")
    @NotBlank(message = "列车号别名不能为空")
    private String aliasTrain;

    @JsonProperty("Num_Vehicle")
    @NotBlank(message = "车辆号不能为空")
    private String numVehicle;

    @JsonProperty("Alias_Vehicle")
    @NotBlank(message = "车辆别名不能为空")
    private String aliasVehicle;

    @JsonProperty("Category")
    @NotBlank(message = "项目信息不能为空")
    private String category;

    @JsonProperty("Sub_Category")
    @NotBlank(message = "分类信息不能为空")
    private String subCategory;

    @JsonProperty("Alarm_Grade")
    @NotBlank(message = "等级信息不能为空")
    private String alarmGrade;

    @JsonProperty("Component")
    @NotBlank(message = "部件信息不能为空")
    private String component;

    @JsonProperty("Subpart")
    @NotBlank(message = "子部件信息不能为空")
    private String subpart;

    @JsonProperty("Alias_Subpart")
    @NotBlank(message = "子部件别名不能为空")
    private String aliasSubpart;

    @JsonProperty("Num_Bogie")
    private String numBogie;

    @JsonProperty("Num_Axle")
    private String numAxle;

    @JsonProperty("Num_Location")
    private String numLocation;

    @JsonProperty("Alias_Location")
    private String aliasLocation;

    @JsonProperty("Code_QZ")
    private String codeQZ;

}
