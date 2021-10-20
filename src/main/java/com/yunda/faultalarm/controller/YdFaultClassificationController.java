package com.yunda.faultalarm.controller;


import com.alibaba.excel.EasyExcel;
import com.yunda.faultalarm.biz.dto.BaseResponse;
import com.yunda.faultalarm.biz.dto.FaultClassificationData;
import com.yunda.faultalarm.biz.listener.UploadFaultClassificationListener;
import com.yunda.faultalarm.biz.service.IYdFaultClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 故障分类信息表 前端控制器
 * </p>
 *
 * @author GYK
 * @since 2021-10-14
 */
@RestController
@RequestMapping("/fault-classification")
public class YdFaultClassificationController {
    @Autowired
    private IYdFaultClassificationService ydFaultClassificationService;

    /**
     * 文件上传
     * <p>1. 创建excel对应的实体对象 参照{@link com.yunda.faultalarm.biz.dto.FaultClassificationData}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UploadFaultClassificationListener}
     * <p>3. 直接读即可
     */
    @PostMapping("/upload")
    @ResponseBody
    public BaseResponse upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), FaultClassificationData.class, new UploadFaultClassificationListener(ydFaultClassificationService)).sheet().doRead();
        return BaseResponse.buildSuccess();
    }

}
