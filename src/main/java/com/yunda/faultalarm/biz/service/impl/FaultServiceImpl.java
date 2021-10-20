package com.yunda.faultalarm.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunda.faultalarm.biz.service.IFaultService;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdFaultClassificationService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdFaultClassification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gyk
 */
@Service
public class FaultServiceImpl implements IFaultService {
    @Autowired
    private IYdFaultClassificationService faultClassificationService;
    @Autowired
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;

    @Override
    public boolean processFault(String cityCode, String line, String fault) {
        QueryWrapper<YdFaultClassification> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<YdFaultClassification> lambdaQueryWrapper = queryWrapper.lambda().eq(YdFaultClassification::getLine, line).eq(YdFaultClassification::getFaultDescription, fault);
        if (StringUtils.isNotBlank(cityCode)){
            lambdaQueryWrapper.eq(YdFaultClassification::getCityCode,cityCode);
        }
        List<YdFaultClassification> list = faultClassificationService.list(lambdaQueryWrapper);
        QueryWrapper<YdCategoryPhoneConfig> categoryPhoneConfigQuery = new QueryWrapper<>();
        list.forEach(ydFaultClassification -> {
            categoryPhoneConfigQuery.lambda().eq(YdCategoryPhoneConfig::getCityCode,ydFaultClassification.getCityCode())
                    .eq(YdCategoryPhoneConfig::getCategory,ydFaultClassification.getCategoryName())
                    .eq(YdCategoryPhoneConfig::getLine,ydFaultClassification.getLine());
            List<YdCategoryPhoneConfig> categoryPhoneConfigs = categoryPhoneConfigService.list(categoryPhoneConfigQuery);
            //todo 发送短信

            categoryPhoneConfigQuery.clear();
        });
        return false;
    }
}
