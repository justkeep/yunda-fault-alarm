package com.yunda.faultalarm.controller;


import com.yunda.faultalarm.biz.dto.BaseResponse;
import com.yunda.faultalarm.biz.dto.YunDaFaultMessageDTO;
import com.yunda.faultalarm.biz.service.IFaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * <p>
 * 短信发送日志记录表 前端控制器
 * </p>
 *
 * @author gyk
 * @since 2021-10-14
 */
@RestController
@RequestMapping("/yunda-fault")
public class FaultController {

    @Autowired
    private IFaultService faultService;


    @PostMapping("/send")
    @ResponseBody
    public BaseResponse sendMessage(@Valid @RequestBody YunDaFaultMessageDTO message) {
        boolean result = faultService.processFault(message.getCityCode(), message.getLine(), message.getFault());
        return BaseResponse.buildSuccess(result);
    }
}
