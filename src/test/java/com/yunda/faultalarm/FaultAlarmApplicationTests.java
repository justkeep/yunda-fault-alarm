package com.yunda.faultalarm;

import com.yunda.faultalarm.biz.dto.YunDaFaultMessageDTO;
import com.yunda.faultalarm.biz.service.IYdWangyiConfigService;
import com.yunda.faultalarm.controller.YdFaultReceiveController;
import com.yunda.faultalarm.dal.model.YdWangyiConfig;
import com.yunda.faultalarm.utils.CheckSumBuilder;
import com.yunda.faultalarm.utils.MessageUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FaultAlarmApplicationTests {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MessageUtil messageUtil;
    @Autowired
    private YdFaultReceiveController ydFaultReceiveController;
    @Autowired
    private IYdWangyiConfigService ydWangyiConfigService;


    @Test
    public void test(){
        String s = "{\n" +
                "\"Alarm_Time\":\"2021-10-25 18:25:48\",\n" +
                "\"Num_line\":\"21018\",\n" +
                "\"Num_Train\":\"35\",\n" +
                "\"Alias_Train\":\"C36\",\n" +
                "\"Num_Vehicle\":\"1\",\n" +
                "\"Alias_Vehicle\":\"Tc1\",\n" +
                "\"Category\":\"1\",\n" +
                "\"Sub_Category\":\"1\",\n" +
                "\"Alarm_Grade\":\"0\",\n" +
                "\"Component\":\"1\",\n" +
                "\"Subpart\":\"1\",\n" +
                "\"Alias_Subpart\":\"8位轴端轴承\",\n" +
                "\"Num_Bogie\":\"I\",\n" +
                "\"Num_Axle\":\"1\",\n" +
                "\"Num_Location\":\"1\",\n" +
                "\"Alias_Location\":\"4轴齿轮箱小齿轮电机侧\"," +
                "\"Code_QZ\":\"1\"\n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json;charset=utf-8");
        HttpEntity<String> res = restTemplate
                .exchange("http://8.136.159.209:8080/yunda-fault-send", HttpMethod.POST, new HttpEntity<>(s, headers),
                        String.class);
        System.out.println(res);
    }
}
