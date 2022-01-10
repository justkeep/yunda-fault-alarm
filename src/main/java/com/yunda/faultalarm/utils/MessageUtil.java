package com.yunda.faultalarm.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yunda.faultalarm.biz.dto.YunDaFaultMessageDTO;
import com.yunda.faultalarm.biz.exception.BizException;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import com.yunda.faultalarm.biz.service.IYdWangyiConfigService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdMsgLog;
import com.yunda.faultalarm.dal.model.YdWangyiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 短信工具类
 *
 * @author gyk
 */
@Component
@Slf4j
public class MessageUtil {

    @Value("${wangyiyun.service-url}")
    private String serviceUrl;

    @Value("${wangyiyun.appkey}")
    private String appKey;

    @Value("${wangyiyun.templateid}")
    private String templateId;

    @Value("${wangyiyun.app-secret}")
    private String appSecret;

    @Value("${wangyiyun.nonce}")
    private String nonce;

    @Autowired
    private IYdMsgLogService msgLogService;
    @Autowired
    private IYdWangyiConfigService ydWangyiConfigService;

    public boolean sendMessage(String content, YunDaFaultMessageDTO yunDaFaultMessageDTO, YdCategoryPhoneConfig categoryPhoneConfig, String phone) {
        //获取网易云应用信息
        List<YdWangyiConfig> wangyiConfigs = ydWangyiConfigService.list();
        List<String> mobileList = Arrays.asList(phone);
        boolean sendStatus = false;
        int i = 0;
        //遍历配置的所有网易云应用
        while (!sendStatus && i<wangyiConfigs.size()){
            String sendResult = "";
            try {
                String result = sendMessageByWangYi(content, mobileList,wangyiConfigs.get(0));
                HashMap<String, Object> hashMap = JSON.parseObject(result, HashMap.class);
                String returnCode = hashMap.get("code").toString();
                sendStatus = "200".equals(returnCode) ;
                if (sendStatus){
                    //成功保存结果
                    saveMsgLogRecord(yunDaFaultMessageDTO, categoryPhoneConfig.getLineName(), content, mobileList, "success", result, categoryPhoneConfig.getId());
                }else {
                    if (i == wangyiConfigs.size() - 1){
                        //最后一次尝试发送短信也失败了则记录短信发送失败结果
                        saveMsgLogRecord(yunDaFaultMessageDTO, categoryPhoneConfig.getLineName(), content, mobileList, "fail", result, categoryPhoneConfig.getId());
                    }
                }
            } catch (Exception e) {
                if (i == (wangyiConfigs.size()-1)){
                    saveMsgLogRecord(yunDaFaultMessageDTO, categoryPhoneConfig.getLineName(), content, mobileList, "fail", sendResult, categoryPhoneConfig.getId());
                }
            }finally {
                i++;
            }
        }
        return true;
    }

    /**
     * 发送短信返回状态码
     *
     * @param content
     * @param mobileList
     * @return
     */
    @SneakyThrows
    public String sendMessageByWangYi(String content, List<String> mobileList,YdWangyiConfig wangyiConfig) {
        String mobiles = JSON.parseArray(JSON.toJSONString(mobileList)).toJSONString();
        List<String> paramList = new ArrayList<>();
        if (content.length() > 20) {
            paramList.add(content.substring(0, 20));
            String newContent = content.substring(20);
            int paramNumber = newContent.length() / 30;
            int i1 = content.length() % 30;
            if (i1 != 0) {
                paramNumber++;
            }
            if (paramNumber > 1) {
                for (int i = 0; i < paramNumber; i++) {
                    int beginIndex = i * 30;
                    int endIndex = i * 30 + 30;
                    if (endIndex >= newContent.length()) {
                        paramList.add(newContent.substring(beginIndex));
                    } else {
                        paramList.add(newContent.substring(beginIndex, endIndex));
                    }
                }
            } else {
                paramList.add(newContent);
            }
        } else {
            paramList.add(content);
        }
        while (paramList.size() < wangyiConfig.getParamNumber()) {
            paramList.add("");
        }
        String params = JSON.parseArray(JSON.toJSONString(paramList)).toJSONString();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(wangyiConfig.getRequestUrl());
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        /*
         * 参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
         */
        String checkSum = CheckSumBuilder.getCheckSum(wangyiConfig.getAppSecret(), nonce, curTime);
        // 设置请求的header
        httpPost.addHeader("AppKey", wangyiConfig.getAppKey());
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的的参数，requestBody参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        /*
         * 1.如果是模板短信，请注意参数mobile是有s的，详细参数配置请参考“发送模板短信文档”
         * 2.参数格式是jsonArray的格式，例如 "['13888888888','13666666666']"
         * 3.params是根据你模板里面有几个参数，那里面的参数也是jsonArray格式
         */
        nvps.add(new BasicNameValuePair("templateid", wangyiConfig.getTemplateId()));
        nvps.add(new BasicNameValuePair("mobiles", mobiles));
        nvps.add(new BasicNameValuePair("params", params));
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("远程调用网易云接口出错" + e);
            throw e;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //记录日志
    public void saveMsgLogRecord(YunDaFaultMessageDTO yunDaFaultMessageDTO, String lineName, String message, List<String> phones, String messageStatus, String reason, Integer configId) {
        String finalMessage = message;
        String finalMessageStatus = messageStatus;
        List<YdMsgLog> msgLogs = new ArrayList<>();
        msgLogs.addAll(phones.stream().map(phone -> {
            YdMsgLog ydMsgLog = new YdMsgLog();
            ydMsgLog.setLineName(lineName);
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
            if (messageStatus.equals("fail")) {
                HashMap<String, Object> hashMap = JSON.parseObject(reason, HashMap.class);
                String returnCode = hashMap.get("code").toString();
                ydMsgLog.setReason("调用网易云接口发送短信失败：错误码:" + reason);
            }
            ydMsgLog.setExtInfo(reason);
            ydMsgLog.setConfigId(configId);
            return ydMsgLog;
        }).collect(Collectors.toList()));
        msgLogService.saveBatch(msgLogs);
    }
}
