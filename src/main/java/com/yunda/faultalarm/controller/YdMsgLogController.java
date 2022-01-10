package com.yunda.faultalarm.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunda.faultalarm.biz.dto.BaseResponse;
import com.yunda.faultalarm.biz.dto.MsgLogDTO;
import com.yunda.faultalarm.biz.dto.QueryMsgParams;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdMsgLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 短信发送日志记录相关接口
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class YdMsgLogController {

    @Autowired
    private IYdMsgLogService msgLogService;
    @Autowired
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/msg_log_page")
    public BaseResponse msgLogPage(QueryMsgParams queryMsgParams){
        QueryWrapper<YdMsgLog> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<YdMsgLog> lambdaQueryWrapper = queryWrapper.lambda()
                .in(YdMsgLog::getSendStatus, Arrays.asList("success", "fail"))
                .orderByDesc(YdMsgLog::getSendTime);
          if (StringUtils.isNotBlank(queryMsgParams.getLineName())){
              lambdaQueryWrapper.like(YdMsgLog::getLineName,queryMsgParams.getLineName());
          }
          if (StringUtils.isNotBlank(queryMsgParams.getPhone())){
              lambdaQueryWrapper.like(YdMsgLog::getPhone,queryMsgParams.getPhone());
          }
          if (StringUtils.isNotBlank(queryMsgParams.getStartTime())){
              lambdaQueryWrapper.ge(YdMsgLog::getSendTime,queryMsgParams.getStartTime());
          }
          if (StringUtils.isNotBlank(queryMsgParams.getEndTime())){
              lambdaQueryWrapper.le(YdMsgLog::getSendTime,queryMsgParams.getEndTime());
          }
        Page<YdMsgLog> page = new Page<>(queryMsgParams.getPageNum(),queryMsgParams.getPageSize());
        msgLogService.page(page,lambdaQueryWrapper);
        MsgLogDTO msgLogDTO = new MsgLogDTO();
        msgLogDTO.setTotalCount(page.getTotal());
        msgLogDTO.setPageNum(page.getCurrent());
        msgLogDTO.setPageSize(page.getSize());
        List<YdMsgLog> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            msgLogDTO.setData(records.stream().map(record->{
                MsgLogDTO.MsgDetail msgDetail = new MsgLogDTO.MsgDetail();
                msgDetail.setId(record.getId());
                msgDetail.setLineName(record.getLineName());
                msgDetail.setLineCode(record.getLineCode());
                msgDetail.setContent(record.getAlarmTime()+","+record.getContent());
                msgDetail.setPhone(record.getPhone());
                msgDetail.setSendTime(record.getSendTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                msgDetail.setSendStatus("success".equals(record.getSendStatus())? "成功":"失败");
                msgDetail.setReason(record.getReason());
                return msgDetail;
            }).collect(Collectors.toList()));
        }else {
            msgLogDTO.setData(Collections.EMPTY_LIST);
        }
        return BaseResponse.buildSuccess(msgLogDTO);
    }

    @GetMapping("/query_line_name")
    public BaseResponse queryLineName(){
        QueryWrapper<YdCategoryPhoneConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(YdCategoryPhoneConfig::getLineName)
                .eq(YdCategoryPhoneConfig::getDelFlag,0)
                .groupBy(YdCategoryPhoneConfig::getLineName);
        List<YdCategoryPhoneConfig> list = categoryPhoneConfigService.list(queryWrapper);
        List<String> lineNameList = list.stream().map(YdCategoryPhoneConfig::getLineName).collect(Collectors.toList());
        return BaseResponse.buildSuccess(lineNameList);
    }
}
