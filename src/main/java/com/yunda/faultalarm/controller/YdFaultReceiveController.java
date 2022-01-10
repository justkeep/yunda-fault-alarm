package com.yunda.faultalarm.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunda.faultalarm.biz.dto.YunDaFaultMessageDTO;
import com.yunda.faultalarm.biz.dto.YundaFaultResponse;
import com.yunda.faultalarm.biz.exception.BizException;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdFaultGradeConfigService;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdFaultGradeConfig;
import com.yunda.faultalarm.dal.model.YdMsgLog;
import com.yunda.faultalarm.enums.ResultCode;
import com.yunda.faultalarm.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class YdFaultReceiveController {
    @Autowired
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;
    @Autowired
    private IYdFaultGradeConfigService gradeConfigService;
    @Autowired
    private IYdMsgLogService msgLogService;
    @Autowired
    private MessageUtil messageUtil;
    @Value("#{'${yunda.category}'.split(',')}")
    private List<String> categories;
    @Value("#{'${yunda.subCategory}'.split(',')}")
    private List<String> subCategories;
    @Value("#{'${yunda.alarmGrade}'.split(',')}")
    private List<String> alarmGrades;
    @Value("#{'${yunda.component}'.split(',')}")
    private List<String> components;

    //走行部模板
    private final static String TRAVELING_TEMPLATE = "%s列车%s车%s%s";
    //复合传感器模板
    private final static String COMPOUND_TEMPLATE = "%s列车%s车%s轴%s复合传感器故障";
    //前置处理器故障
    private final static String PRE_TEMPLATE = "%s列车%s车%s号前置处理器故障";
    //失稳模板
    private final static String INSTABILITY_TEMPLATE = "%s列车%s车%s架%s";
    //脱轨模板
    private final static String DERAILMENT_TEMPLATE = "%列车%s车%s车轮脱轨报警";

    @PostMapping("/yunda-fault-send")
    public YundaFaultResponse sendYundaFault(@Valid @RequestBody YunDaFaultMessageDTO yunDaFaultMessageDTO) {
        //关键参数校验
        if (!categories.contains(yunDaFaultMessageDTO.getCategory())) {
            throw new BizException("Category仅接收以下枚举值：" + JSON.toJSON(categories));
        }
        if (!subCategories.contains(yunDaFaultMessageDTO.getSubCategory())) {
            throw new BizException("Sub_Category仅接收以下枚举值：" + JSON.toJSON(subCategories));
        }
        if (!alarmGrades.contains(yunDaFaultMessageDTO.getAlarmGrade())) {
            throw new BizException("Alarm_Grade仅接收以下枚举值：" + JSON.toJSON(alarmGrades));
        }
        if (!components.contains(yunDaFaultMessageDTO.getComponent())) {
            throw new BizException("Component仅接收以下枚举值：" + JSON.toJSON(components));
        }
        //根据线路编码，等级，部件查询配置，判断是否需要发送短信
        String grade = yunDaFaultMessageDTO.getCategory() + yunDaFaultMessageDTO.getSubCategory() + yunDaFaultMessageDTO.getAlarmGrade();
        QueryWrapper<YdCategoryPhoneConfig> categoryPhoneConfigQueryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<YdCategoryPhoneConfig> lambdaQueryWrapper = categoryPhoneConfigQueryWrapper.lambda()
                .eq(YdCategoryPhoneConfig::getLineCode, yunDaFaultMessageDTO.getLineCode())
                .eq(YdCategoryPhoneConfig::getDelFlag, 0)
                .like(YdCategoryPhoneConfig::getGrade, grade);
        //0表示不验证部件信息
        String noComponent = "0";
        if (!noComponent.equals(yunDaFaultMessageDTO.getComponent())) {
            lambdaQueryWrapper.like(YdCategoryPhoneConfig::getComponent, yunDaFaultMessageDTO.getComponent());
        }
        List<YdCategoryPhoneConfig> categoryPhoneConfigs = categoryPhoneConfigService.list(lambdaQueryWrapper);
        QueryWrapper<YdFaultGradeConfig> gradeConfigQueryWrapper = new QueryWrapper<>();
        gradeConfigQueryWrapper.lambda().in(YdFaultGradeConfig::getCodeValue, Arrays.asList(grade,yunDaFaultMessageDTO.getComponent()));
        List<YdFaultGradeConfig> gradeConfigs = gradeConfigService.list(gradeConfigQueryWrapper);
        Map<String, YdFaultGradeConfig> collect = gradeConfigs.stream().collect(Collectors.toMap(YdFaultGradeConfig::getCodeValue, Function.identity(), (e1, e2) -> e2));
        //根据项目：分类：等级 判断使用什么短信模板，组装短信内容
        String message = getMessageTemplate(yunDaFaultMessageDTO, collect.get(grade));
        String reason = "线路:%s,故障等级:%s,%s";
        String componentShowName = Objects.nonNull(collect.get(yunDaFaultMessageDTO.getComponent()))?
                new StringBuilder("部件:").append(collect.get(yunDaFaultMessageDTO.getComponent()).getShowName()).append(",").toString():"";
        String resultReason = String.format(reason, yunDaFaultMessageDTO.getLineCode(), collect.get(grade).getShowName(),componentShowName);
        if (CollectionUtils.isEmpty(categoryPhoneConfigs)) {
            saveMsgLogRecord(yunDaFaultMessageDTO, yunDaFaultMessageDTO.getLineCode(), resultReason+"未配置短信预警功能", message, null, null);
            return YundaFaultResponse.buildSuccess(true);
        }
        //根据配置发送短信
        categoryPhoneConfigs.forEach(categoryPhoneConfig -> {
            String lineName = categoryPhoneConfig.getLineName();
            //发送开关已关闭
            if (categoryPhoneConfig.getPushMsgFlag() == 0) {
                saveMsgLogRecord(yunDaFaultMessageDTO, lineName, resultReason+"未开启短信预警功能", message, categoryPhoneConfig.getId(), null);
                return;
            }
            if (StringUtils.isNotBlank(message)) {
                List<String> mobiles = Arrays.asList(categoryPhoneConfig.getPhones().split(","));
                String finalMessage = message;
                mobiles.forEach(phone -> {
                    String judgeResult = judgeMessageSendCondition(yunDaFaultMessageDTO, finalMessage, categoryPhoneConfig, phone);
                    if (!StringUtils.isNotBlank(judgeResult)) {
                        //发送短信
                        messageUtil.sendMessage(finalMessage, yunDaFaultMessageDTO, categoryPhoneConfig, phone);
                    } else {
                        //不发送短信仅记录调用日志
                        saveMsgLogRecord(yunDaFaultMessageDTO, lineName, judgeResult, finalMessage, categoryPhoneConfig.getId(), phone);
                    }
                });
            } else {
                //不发送短信仅记录调用日志
                saveMsgLogRecord(yunDaFaultMessageDTO, lineName, "根据故障等级没有找到对应的短信模板", message, categoryPhoneConfig.getId(), null);
            }
        });
        return YundaFaultResponse.buildSuccess(true);
    }

    private String judgeMessageSendCondition(YunDaFaultMessageDTO yunDaFaultMessageDTO, String message, YdCategoryPhoneConfig categoryPhoneConfig, String phone) {
        QueryWrapper<YdMsgLog> msgLogQueryWrapper = new QueryWrapper<>();
        msgLogQueryWrapper.lambda()
                .eq(YdMsgLog::getSendStatus, "success")
                .eq(YdMsgLog::getLineCode, yunDaFaultMessageDTO.getLineCode())
                .eq(YdMsgLog::getContent, message)
                .eq(YdMsgLog::getConfigId, categoryPhoneConfig.getId())
                .eq(YdMsgLog::getPhone, phone)
                .like(YdMsgLog::getSendTime, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .orderByDesc(YdMsgLog::getSendTime);
        List<YdMsgLog> list = msgLogService.list(msgLogQueryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            //判断是否满足短信发送频率
            LocalDateTime alarmTime = LocalDateTime.parse(yunDaFaultMessageDTO.getAlarmTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime preAlarmTime = LocalDateTime.parse(list.get(0).getAlarmTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            boolean cutOffFlag = preAlarmTime.plusHours(categoryPhoneConfig.getCutOff()).isBefore(alarmTime)
                    || preAlarmTime.plusHours(categoryPhoneConfig.getCutOff()).isEqual(alarmTime);
            if (!cutOffFlag) {
                //不满足短信发送频率，不发送短信
                return "本次报警时间:" + yunDaFaultMessageDTO.getAlarmTime() +
                        ",上次报警短信发送时间为:" + preAlarmTime +
                        ",短信发送频率:" + categoryPhoneConfig.getCutOff() + "小时一次";
            } else {
                //判断是否超过短信发送次数
                Map<String, List<YdMsgLog>> groupByPhone = list.stream().collect(Collectors.groupingBy(YdMsgLog::getPhone));
                boolean frequencyFlag = true;
                for (List<YdMsgLog> msgLogList : groupByPhone.values()) {
                    if (msgLogList.size() >= categoryPhoneConfig.getFrequency()) {
                        frequencyFlag = false;
                        break;
                    }
                }
                //超过短信发送次数,不发送短信
                if (!frequencyFlag) {
                    return "本次报警时间:" + yunDaFaultMessageDTO.getAlarmTime() +
                            ",已经超过今日短信发送次数:" + categoryPhoneConfig.getFrequency();
                }
            }
        }
        return null;
    }

    /**
     * 选取模板
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
    private String getMessageTemplate(YunDaFaultMessageDTO yunDaFaultMessageDTO, YdFaultGradeConfig gradeConfig) {
        String message = "";
        //获取故障等级信息
        String grade = yunDaFaultMessageDTO.getCategory() + yunDaFaultMessageDTO.getSubCategory() + yunDaFaultMessageDTO.getAlarmGrade();
//        QueryWrapper<YdFaultGradeConfig> gradeConfigQueryWrapper = new QueryWrapper<>();
//        gradeConfigQueryWrapper.lambda().eq(YdFaultGradeConfig::getCodeValue, grade);
//        YdFaultGradeConfig gradeConfig = gradeConfigService.getOne(gradeConfigQueryWrapper, false);
        //前置处理器故障模板  【列车别名】列车【车辆别名】车【前置处理器号】号前置处理器故障
        if ("110".equals(grade)) {
            if (!StringUtils.isNotBlank(yunDaFaultMessageDTO.getCodeQZ())) {
                //前置处理器号不能为空
                throw new BizException(ResultCode.CODE_QZ_ERROR.getValue(), "前置处理器故障：前置处理器号不能为空!");
            }
            message = String.format(PRE_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getCodeQZ());
        }
        //复合传感器故障 【列车别名】列车【车辆别名】车【测点号别名】复合传感器故障
        if ("120".equals(grade)) {
            if (!StringUtils.isNotBlank(yunDaFaultMessageDTO.getAliasLocation())) {
                //测点号别名不能为空
                throw new BizException(ResultCode.ALIAS_LOCATION_ERROR.getValue(), "复合传感器故障：测点号别名不能为空!");
            }
            if (!StringUtils.isNotBlank(yunDaFaultMessageDTO.getNumAxle())) {
                //测点号别名不能为空
                throw new BizException(ResultCode.ALIAS_LOCATION_ERROR.getValue(), "复合传感器故障：轴号不能为空!");
            }
            message = String.format(COMPOUND_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getNumAxle(), yunDaFaultMessageDTO.getAliasLocation());
        }
        //走行部模板  【列车别名】列车【车辆别名】车【子部件别名】【分类】【等级】
        if ("211".equals(grade) || "212".equals(grade) || "221".equals(grade) || "222".equals(grade) || "223".equals(grade)) {
            message = String.format(TRAVELING_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getAliasSubpart(), gradeConfig.getDescription());
        }
        //失稳模板 【列车别名】列车【车辆别名】车【架号】架【等级】
        if ("301".equals(grade) || "302".equals(grade)) {
            if (!StringUtils.isNotBlank(yunDaFaultMessageDTO.getNumBogie())) {
                //架号不能为空
                throw new BizException(ResultCode.NUM_BOGIE_ERROR.getValue(), "失稳故障：架号不能为空!");
            }
            message = String.format(INSTABILITY_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getNumBogie(), gradeConfig.getDescription());
        }
        //脱轨模板  【列车别名】列车【车辆别名】车【测点号别名】车轮脱轨报警
        if ("402".equals(grade)) {
            if (!StringUtils.isNotBlank(yunDaFaultMessageDTO.getAliasLocation())) {
                //测点号别名不能为空
                throw new BizException(ResultCode.ALIAS_LOCATION_ERROR.getValue(), "脱轨故障：测点号别名不能为空!");
            }
            message = String.format(DERAILMENT_TEMPLATE, yunDaFaultMessageDTO.getAliasTrain(), yunDaFaultMessageDTO.getAliasVehicle(), yunDaFaultMessageDTO.getAliasLocation());
        }
        return message;
    }

    //记录日志
    private void saveMsgLogRecord(YunDaFaultMessageDTO yunDaFaultMessageDTO, String lineName, String reason, String content, Integer configId, String phone) {
        YdMsgLog ydMsgLog = new YdMsgLog();
        ydMsgLog.setLineCode(yunDaFaultMessageDTO.getLineCode());
        ydMsgLog.setLineName(lineName);
        ydMsgLog.setContent(StringUtils.isNotBlank(content) ? content : "");
        ydMsgLog.setPhone(StringUtils.isNotBlank(phone) ? phone : "");
        ydMsgLog.setSendStatus("fail");
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
        ydMsgLog.setReason(reason);
        ydMsgLog.setConfigId(configId);
        msgLogService.save(ydMsgLog);
    }
}
