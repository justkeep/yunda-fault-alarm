package com.yunda.faultalarm.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunda.faultalarm.biz.dto.BaseResponse;
import com.yunda.faultalarm.biz.dto.YunDaFaultMessageDTO;
import com.yunda.faultalarm.biz.dto.YundaFaultResponse;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdFaultGradeConfigService;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdFaultGradeConfig;
import com.yunda.faultalarm.dal.model.YdMsgLog;
import com.yunda.faultalarm.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障等级配置表 前端控制器
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@RestController
@CrossOrigin(origins = "*")
public class YdFaultReceiveController {
    @Autowired
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;
    @Autowired
    private IYdFaultGradeConfigService gradeConfigService;
    @Autowired
    private IYdMsgLogService msgLogService;
    @Autowired
    private MessageUtil messageUtil;

    //走行部模板
    private final static String TRAVELING_TEMPLATE = "%s列车%s车%s %s";
    //复合传感器模板
    private final static String COMPOUND_TEMPLATE = "%s列车%s车%s复合传感器故障";
    //前置处理器故障
    private final static String PRE_TEMPLATE = "%s列车%s车%s号前置处理器故障";
    //失稳模板
    private final static String INSTABILITY_TEMPLATE = "%s列车%s车%s架%s";
    //脱轨模板
    private final static String DERAILMENT_TEMPLATE = "%列车%s车%s车轮脱轨报警";

    @PostMapping("/yunda-fault-send")
    public YundaFaultResponse sendYundaFault(@Valid @RequestBody YunDaFaultMessageDTO yunDaFaultMessageDTO) {
        //根据线路编码，等级，部件查询配置，判断是否需要发送短信
        String grade = yunDaFaultMessageDTO.getCategory() + yunDaFaultMessageDTO.getSubCategory() + yunDaFaultMessageDTO.getAlarmGrade();
        QueryWrapper<YdCategoryPhoneConfig> categoryPhoneConfigQueryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<YdCategoryPhoneConfig> lambdaQueryWrapper = categoryPhoneConfigQueryWrapper.lambda()
                .eq(YdCategoryPhoneConfig::getLineCode, yunDaFaultMessageDTO.getLineCode())
                .like(YdCategoryPhoneConfig::getGrade, grade);
        if (StringUtils.isNotBlank(yunDaFaultMessageDTO.getComponent())) {
            lambdaQueryWrapper.like(YdCategoryPhoneConfig::getComponent, yunDaFaultMessageDTO.getComponent());
        }
        YdCategoryPhoneConfig categoryPhoneConfig = categoryPhoneConfigService.getOne(lambdaQueryWrapper, false);

        //根据判断使用什么短信模板，组装短信内容
        /**
         * grade：
         * 项目：分类：等级
         * 110：前置处理器故障
         * 120：复合传感器故障
         * 211：温度预警
         * 212：温度报警
         * 221：振动预警
         * 222：振动I级报警
         * 223：振动II级报警
         * 301：失稳预警
         * 302：失稳报警
         * 402：脱轨
         */
        String message = null;
        if (Objects.nonNull(categoryPhoneConfig)) {
            //获取故障等级信息
            QueryWrapper<YdFaultGradeConfig> gradeConfigQueryWrapper = new QueryWrapper<>();
            gradeConfigQueryWrapper.lambda().eq(YdFaultGradeConfig::getCodeValue, grade);
            YdFaultGradeConfig gradeConfig = gradeConfigService.getOne(gradeConfigQueryWrapper, false);
            //前置处理器故障模板  【列车别名】列车【车辆别名】车【前置处理器号】号前置处理器故障
            if ("110".equals(grade)) {
                message = String.format(PRE_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getCodeQZ());
            }
            //复合传感器故障 【列车别名】列车【车辆别名】车【测点号别名】复合传感器故障
            if ("120".equals(grade)) {
                message = String.format(COMPOUND_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getAliasLocation());
            }
            //走行部模板  【列车别名】列车【车辆别名】车【子部件别名】【分类】【等级】
            if ("211".equals(grade) || "212".equals(grade) || "221".equals(grade) || "222".equals(grade) || "223".equals(grade)) {
                message = String.format(TRAVELING_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getAliasSubpart(), gradeConfig.getDescription());
            }
            //失稳模板 【列车别名】列车【车辆别名】车【架号】架【等级】
            if ("301".equals(grade) || "302".equals(grade)) {
                message = String.format(INSTABILITY_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getNumBogie(), gradeConfig.getDescription());
            }
            //脱轨模板  【列车别名】列车【车辆别名】车【测点号别名】车轮脱轨报警
            if ("402".equals(grade)) {
                message = String.format(DERAILMENT_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getNumLocation());
            }
        }

        if (StringUtils.isNotBlank(message)) {
            QueryWrapper<YdMsgLog> msgLogQueryWrapper = new QueryWrapper<>();
            msgLogQueryWrapper.lambda()
                    .eq(YdMsgLog::getSendStatus, "success")
                    .eq(YdMsgLog::getLineCode, yunDaFaultMessageDTO.getLineCode())
                    .eq(YdMsgLog::getContent, message)
                    .like(YdMsgLog::getSendTime, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .orderByDesc(YdMsgLog::getSendTime);
            List<YdMsgLog> list = msgLogService.list(msgLogQueryWrapper);
            List<String> phones = Arrays.asList(categoryPhoneConfig.getPhones().split(","));
            boolean messageStatus = true;
            if (CollectionUtils.isEmpty(list)) {
                //todo 发送短信
                messageStatus = messageUtil.sendMessage(phones, message);
            } else {
                //判断是否满足短信发送频率
                boolean cutOffFlag = LocalDateTime.now().plusHours(categoryPhoneConfig.getCutOff()).isAfter(list.get(0).getSendTime())
                        || LocalDateTime.now().plusHours(categoryPhoneConfig.getCutOff()).isEqual(list.get(0).getSendTime());
                if (cutOffFlag){
                    //判断是否超过短信发送次数
                    Map<String, List<YdMsgLog>> groupByPhone = list.stream().collect(Collectors.groupingBy(YdMsgLog::getPhone));
                    boolean frequencyFlag = true;
                    for (List<YdMsgLog> msgLogList :groupByPhone.values()){
                        if (msgLogList.size()>categoryPhoneConfig.getFrequency()){
                            frequencyFlag = false;
                            break;
                        }
                    }
                    if (frequencyFlag){
                        //todo 发送短信
                        messageStatus = messageUtil.sendMessage(phones,message);
                    }
                }
            }
            //记录日志
            saveMsgLogRecord(yunDaFaultMessageDTO, categoryPhoneConfig, message, phones, messageStatus ? "success" : "fail");
        }

        saveMsgLogRecord(yunDaFaultMessageDTO, categoryPhoneConfig, message, null, "inexecution");
        return YundaFaultResponse.buildSuccess(true);
    }

    //记录日志
    private void saveMsgLogRecord(YunDaFaultMessageDTO yunDaFaultMessageDTO, YdCategoryPhoneConfig categoryPhoneConfig, String message, List<String> phones, String messageStatus) {
        String finalMessage = message;
        String finalMessageStatus = messageStatus;
        List<YdMsgLog> msgLogs = new ArrayList<>();
        if (CollectionUtils.isEmpty(phones)){
            YdMsgLog ydMsgLog = new YdMsgLog();
            ydMsgLog.setLineCode(yunDaFaultMessageDTO.getLineCode());
            ydMsgLog.setLineName("");
            ydMsgLog.setContent("");
            ydMsgLog.setPhone("");
            ydMsgLog.setSendStatus(finalMessageStatus);
            ydMsgLog.setAlarmTime(yunDaFaultMessageDTO.getAlarmTime());
            ydMsgLog.setNumTrain(yunDaFaultMessageDTO.getNumTrain());
            ydMsgLog.setAliasTrain(yunDaFaultMessageDTO.getAliasTrain());
            ydMsgLog.setNumVehicle(yunDaFaultMessageDTO.getNumVehicle());
            ydMsgLog.setAliasVehicle(yunDaFaultMessageDTO.getAliasVehicle());
            ydMsgLog.setCategory(yunDaFaultMessageDTO.getCategory());
            ydMsgLog.setSubCategory(yunDaFaultMessageDTO.getSubCategory());
            ydMsgLog.setAlarmGrade(yunDaFaultMessageDTO.getAlarmGrade());
            ydMsgLog.setComponent(yunDaFaultMessageDTO.getComponent());
            ydMsgLog.setSubpart(yunDaFaultMessageDTO.getSubpart());
            ydMsgLog.setAliasSubpart(yunDaFaultMessageDTO.getAliasSubpart());
            ydMsgLog.setNumBogie(yunDaFaultMessageDTO.getNumBogie());
            ydMsgLog.setNumAxle(yunDaFaultMessageDTO.getNumAxle());
            ydMsgLog.setNumLocation(yunDaFaultMessageDTO.getNumLocation());
            ydMsgLog.setAliasLocation(yunDaFaultMessageDTO.getAliasLocation());
            ydMsgLog.setCodeQz(yunDaFaultMessageDTO.getCodeQZ());
            msgLogs.add(ydMsgLog);
        }else {
             msgLogs.addAll(phones.stream().map(phone -> {
                YdMsgLog ydMsgLog = new YdMsgLog();
                ydMsgLog.setLineName(categoryPhoneConfig.getLineName());
                ydMsgLog.setLineCode(yunDaFaultMessageDTO.getLineCode());
                ydMsgLog.setContent(finalMessage);
                ydMsgLog.setPhone(phone);
                ydMsgLog.setSendStatus(finalMessageStatus);
                ydMsgLog.setAlarmTime(yunDaFaultMessageDTO.getAlarmTime());
                ydMsgLog.setNumTrain(yunDaFaultMessageDTO.getNumTrain());
                ydMsgLog.setAliasTrain(yunDaFaultMessageDTO.getAliasTrain());
                ydMsgLog.setNumVehicle(yunDaFaultMessageDTO.getNumVehicle());
                ydMsgLog.setAliasVehicle(yunDaFaultMessageDTO.getAliasVehicle());
                ydMsgLog.setCategory(yunDaFaultMessageDTO.getCategory());
                ydMsgLog.setSubCategory(yunDaFaultMessageDTO.getSubCategory());
                ydMsgLog.setAlarmGrade(yunDaFaultMessageDTO.getAlarmGrade());
                ydMsgLog.setComponent(yunDaFaultMessageDTO.getComponent());
                ydMsgLog.setSubpart(yunDaFaultMessageDTO.getSubpart());
                ydMsgLog.setAliasSubpart(yunDaFaultMessageDTO.getAliasSubpart());
                ydMsgLog.setNumBogie(yunDaFaultMessageDTO.getNumBogie());
                ydMsgLog.setNumAxle(yunDaFaultMessageDTO.getNumAxle());
                ydMsgLog.setNumLocation(yunDaFaultMessageDTO.getNumLocation());
                ydMsgLog.setAliasLocation(yunDaFaultMessageDTO.getAliasLocation());
                ydMsgLog.setCodeQz(yunDaFaultMessageDTO.getCodeQZ());
                return ydMsgLog;
            }).collect(Collectors.toList()));
        }
        msgLogService.saveBatch(msgLogs);
    }
}
