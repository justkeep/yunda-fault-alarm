package com.yunda.faultalarm.biz.listener;
import java.time.LocalDateTime;
import java.sql.Blob;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yunda.faultalarm.biz.dto.CategoryPhoneConfigData;
import com.yunda.faultalarm.biz.dto.FaultClassificationData;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdFaultClassificationService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.model.YdFaultClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类电话配置excel解析器
 *
 * @author gyk
 */
public class UploadCategoryPhoneConfigListener extends AnalysisEventListener<CategoryPhoneConfigData> {
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;

    private static final int BATCH_COUNT = 500;
    List<YdCategoryPhoneConfig> list = new ArrayList<YdCategoryPhoneConfig>();

    public UploadCategoryPhoneConfigListener(IYdCategoryPhoneConfigService categoryPhoneConfigService) {
        this.categoryPhoneConfigService = categoryPhoneConfigService;
    }

    @Override
    public void invoke(CategoryPhoneConfigData categoryPhoneConfigData, AnalysisContext analysisContext) {
        YdCategoryPhoneConfig ydCategoryPhoneConfig = new YdCategoryPhoneConfig();
        ydCategoryPhoneConfig.setCityCode(categoryPhoneConfigData.getCityCode());
        ydCategoryPhoneConfig.setLine(categoryPhoneConfigData.getLine());
        ydCategoryPhoneConfig.setCategory(categoryPhoneConfigData.getCategory());
        ydCategoryPhoneConfig.setPhones(categoryPhoneConfigData.getPhone());
        ydCategoryPhoneConfig.setPushMsgFlag(categoryPhoneConfigData.getPushMsgFlag());
        list.add(ydCategoryPhoneConfig);
        if (list.size()>BATCH_COUNT){
            categoryPhoneConfigService.saveBatch(list);
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //确保最后一批数据也写入数据库
        categoryPhoneConfigService.saveBatch(list);
    }
}
