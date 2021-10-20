package com.yunda.faultalarm;

import com.alibaba.excel.EasyExcel;
import com.yunda.faultalarm.biz.dto.CategoryPhoneConfigData;
import com.yunda.faultalarm.biz.dto.FaultClassificationData;
import com.yunda.faultalarm.biz.listener.UploadCategoryPhoneConfigListener;
import com.yunda.faultalarm.biz.listener.UploadFaultClassificationListener;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdFaultClassificationService;
import com.yunda.faultalarm.dal.model.YdMsgLog;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@SpringBootTest
class FaultAlarmApplicationTests {

	@Autowired
	private IYdMsgLogService ydMsgLogService;
	@Autowired
	private IYdFaultClassificationService ydFaultClassificationService;
	@Autowired
	private IYdCategoryPhoneConfigService categoryPhoneConfigService;

	@Test
	void contextLoads() {
		List<YdMsgLog> list = ydMsgLogService.list();
		YdMsgLog byId = ydMsgLogService.getById(1);
		System.out.println(list.size());
	}

	@SneakyThrows
	@Test
	void uploadFaultClassTest(){
		File file = new File("C:\\Users\\www59\\Desktop\\故障分类模板.xlsx");
		FileInputStream fileInputStream = new FileInputStream(file);
		EasyExcel.read(fileInputStream, FaultClassificationData.class, new UploadFaultClassificationListener(ydFaultClassificationService)).sheet().doRead();
	}

	@SneakyThrows
	@Test
	void uploadCategoryPhoneTest(){
		File file = new File("C:\\Users\\www59\\Desktop\\电话号码配置模板.xlsx");
		FileInputStream fileInputStream = new FileInputStream(file);
		EasyExcel.read(fileInputStream, CategoryPhoneConfigData.class, new UploadCategoryPhoneConfigListener(categoryPhoneConfigService)).sheet().doRead();
	}

}
