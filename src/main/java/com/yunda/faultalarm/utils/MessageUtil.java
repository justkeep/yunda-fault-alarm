package com.yunda.faultalarm.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yunda.faultalarm.biz.dto.YunDaFaultMessageDTO;
import com.yunda.faultalarm.biz.exception.BizException;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdMsgLog;
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

    public boolean sendMessage(String content, YunDaFaultMessageDTO yunDaFaultMessageDTO, YdCategoryPhoneConfig categoryPhoneConfig) {
        List<String> mobiles = Arrays.asList(categoryPhoneConfig.getPhones().split(","));
        //todo 手机号，接收者号码列表，JSONArray格式，限制接收者号码个数最多为100个
//        final String MOBILES = "['13888888888','13666666666']";
        int maxMobileSize = 100;
        List<List<String>> partitions = Lists.partition(mobiles, maxMobileSize);
        partitions.forEach(partition->{
            try {
//                String result = sendMessageByWangYi(content, partition);
//                HashMap<String,Object> hashMap = JSON.parseObject(result, HashMap.class);
//                String messageStatus = "200".equals(hashMap.get("code").toString()) ? "success" : "fail";
                String code = "200";
                String messageStatus = code.equals("200") ? "success" : "fail";
                saveMsgLogRecord(yunDaFaultMessageDTO,categoryPhoneConfig.getLineName(),content,partition,messageStatus);
            }catch (Exception e){
                saveMsgLogRecord(yunDaFaultMessageDTO,categoryPhoneConfig.getLineName(),content,partition,"fail");
            }
        });
        return true;
    }

    /**
     * 发送短信返回状态码
     * @param content
     * @param mobileList
     * @return
     */
    private String sendMessageByWangYi(String content, List<String> mobileList) {
        String mobiles = JSON.parseArray(JSON.toJSONString(mobileList)).toJSONString();
        //todo 短信参数列表，用于依次填充模板，JSONArray格式，每个变量长度不能超过30字,对于不包含变量的模板，不填此参数表示模板即短信全文内容
        List<String> paramList = Arrays.asList(content);
//        final String PARAMS = "['xxxx','xxxx']";
        String params = JSON.parseArray(JSON.toJSONString(paramList)).toJSONString();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(serviceUrl);
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        /*
         * 参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
         */
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);
        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
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
        nvps.add(new BasicNameValuePair("templateid", templateId));
        nvps.add(new BasicNameValuePair("mobiles", mobiles));
        nvps.add(new BasicNameValuePair("params", params));
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            // 执行请求
            response = httpClient.execute(httpPost);
            /*
             * 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
             * 2.具体的code有问题的可以参考官网的Code状态表
             */
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("远程调用网易云接口出错" + e);
            throw new BizException("远程调用网易云接口出错");
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
    private void saveMsgLogRecord(YunDaFaultMessageDTO yunDaFaultMessageDTO, String lineName, String message, List<String> phones, String messageStatus) {
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
            return ydMsgLog;
        }).collect(Collectors.toList()));
        msgLogService.saveBatch(msgLogs);
    }
}
