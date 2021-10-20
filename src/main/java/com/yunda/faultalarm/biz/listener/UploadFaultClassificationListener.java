package com.yunda.faultalarm.biz.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yunda.faultalarm.biz.dto.FaultClassificationData;
import com.yunda.faultalarm.biz.service.IYdFaultClassificationService;
import com.yunda.faultalarm.dal.model.YdFaultClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * 故障分类excel解析器
 *
 * @author gyk
 */
public class UploadFaultClassificationListener extends AnalysisEventListener<FaultClassificationData> {
    private IYdFaultClassificationService ydFaultClassificationService;

    private static final int BATCH_COUNT = 500;
    List<YdFaultClassification> list = new ArrayList<YdFaultClassification>();

    public UploadFaultClassificationListener(IYdFaultClassificationService ydFaultClassificationService) {
        this.ydFaultClassificationService = ydFaultClassificationService;
    }

    @Override
    public void invoke(FaultClassificationData faultClassificationData, AnalysisContext analysisContext) {
        YdFaultClassification ydFaultClassification = new YdFaultClassification();
        ydFaultClassification.setCityCode(faultClassificationData.getCityCode());
        ydFaultClassification.setLine(faultClassificationData.getLine());
        ydFaultClassification.setCategoryName(faultClassificationData.getCategory());
        ydFaultClassification.setFaultDescription(faultClassificationData.getFault());
        list.add(ydFaultClassification);
        if (list.size() > BATCH_COUNT) {
            ydFaultClassificationService.saveBatch(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //确保最后一批数据也写入数据库
        ydFaultClassificationService.saveBatch(list);
    }
}
