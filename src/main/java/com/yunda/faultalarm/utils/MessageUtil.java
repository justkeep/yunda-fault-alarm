package com.yunda.faultalarm.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 短信工具类
 *
 * @author gyk
 */
@Component
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

    public boolean sendMessage(List<String> mobiles,String content) {
        //手机号，接收者号码列表，JSONArray格式，限制接收者号码个数最多为100个
//        final String MOBILES = "['13888888888','13666666666']";
        int maxMobileSize = 100;
        List<List<String>> partitions = Lists.partition(mobiles, maxMobileSize);
        partitions.forEach(partition->{
            sendMessageByWangYi(content, partition);
        });
        return true;
    }

    private void sendMessageByWangYi(String content, List<String> mobileList) {
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
            System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
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
}
