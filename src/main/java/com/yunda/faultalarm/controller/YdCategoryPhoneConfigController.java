package com.yunda.faultalarm.controller;


import com.alibaba.excel.EasyExcel;
import com.yunda.faultalarm.biz.dto.BaseResponse;
import com.yunda.faultalarm.biz.dto.CategoryPhoneConfigData;
import com.yunda.faultalarm.biz.dto.FaultClassificationData;
import com.yunda.faultalarm.biz.listener.UploadCategoryPhoneConfigListener;
import com.yunda.faultalarm.biz.listener.UploadFaultClassificationListener;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 故障分类电话配置表 前端控制器
 * </p>
 *
 * @author GYK
 * @since 2021-10-14
 */
@RestController
@RequestMapping("/yd-category-phone-config")
public class YdCategoryPhoneConfigController {

    @Autowired
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;

    @PostMapping("/upload")
    @ResponseBody
    public BaseResponse upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), CategoryPhoneConfigData.class, new UploadCategoryPhoneConfigListener(categoryPhoneConfigService)).sheet().doRead();
        return BaseResponse.buildSuccess();
    }
}
