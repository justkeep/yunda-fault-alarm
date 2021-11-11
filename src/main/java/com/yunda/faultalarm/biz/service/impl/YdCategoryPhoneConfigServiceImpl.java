package com.yunda.faultalarm.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunda.faultalarm.biz.dto.CategoryMsgConfigResultDTO;
import com.yunda.faultalarm.biz.dto.QueryConfigParams;
import com.yunda.faultalarm.biz.dto.SaveCategoryPhoneConfigDTO;
import com.yunda.faultalarm.biz.service.IYdFaultGradeConfigService;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.yunda.faultalarm.dal.mapper.YdCategoryPhoneConfigMapper;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunda.faultalarm.dal.model.YdFaultGradeConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@Service
public class YdCategoryPhoneConfigServiceImpl extends ServiceImpl<YdCategoryPhoneConfigMapper, YdCategoryPhoneConfig> implements IYdCategoryPhoneConfigService {

    @Autowired
    private IYdFaultGradeConfigService gradeConfigService;

    @Override
    public CategoryMsgConfigResultDTO queryCategoryConfigPageList(QueryConfigParams queryConfigParams) {
        QueryWrapper<YdCategoryPhoneConfig> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<YdCategoryPhoneConfig> lambdaQueryWrapper = queryWrapper.lambda().eq(YdCategoryPhoneConfig::getDelFlag, 0);
        if (StringUtils.isNotBlank(queryConfigParams.getLineName())){
            lambdaQueryWrapper.like(YdCategoryPhoneConfig::getLineName, queryConfigParams.getLineName());
        }
        Page<YdCategoryPhoneConfig> page = new Page<>(queryConfigParams.getPageNum(), queryConfigParams.getPageSize());
        this.page(page, lambdaQueryWrapper);
        List<YdCategoryPhoneConfig> records = page.getRecords();
        CategoryMsgConfigResultDTO categoryMsgConfigResultDTO = new CategoryMsgConfigResultDTO();
        categoryMsgConfigResultDTO.setTotalCount(page.getTotal());
        categoryMsgConfigResultDTO.setPageNum(page.getCurrent());
        categoryMsgConfigResultDTO.setPageSize(page.getSize());
        if (!CollectionUtils.isEmpty(records)) {
            List<YdFaultGradeConfig> gradeConfigList = gradeConfigService.list();
            categoryMsgConfigResultDTO.setData(records.stream().map(categoryPhoneConfig -> {
                CategoryMsgConfigResultDTO.CategoryMsgConfigDetail categoryMsgConfigDetail = new CategoryMsgConfigResultDTO.CategoryMsgConfigDetail();
                categoryMsgConfigDetail.setId(categoryPhoneConfig.getId());
                categoryMsgConfigDetail.setLineName(categoryPhoneConfig.getLineName());
                categoryMsgConfigDetail.setLineCode(categoryPhoneConfig.getLineCode());
                categoryMsgConfigDetail.setPhones(categoryPhoneConfig.getPhones());
                categoryMsgConfigDetail.setPushMsgFlag(categoryPhoneConfig.getPushMsgFlag() == 1 ? "发送" : "不发送");
                categoryMsgConfigDetail.setFrequency(categoryPhoneConfig.getFrequency());
                categoryMsgConfigDetail.setCutOff(categoryPhoneConfig.getCutOff());
                categoryMsgConfigDetail.setGrade(getGradeOrComponents(categoryPhoneConfig.getGrade(), gradeConfigList));
                categoryMsgConfigDetail.setComponent(getGradeOrComponents(categoryPhoneConfig.getComponent(), gradeConfigList));
                return categoryMsgConfigDetail;
            }).collect(Collectors.toList()));
        } else {
            categoryMsgConfigResultDTO.setData(Collections.EMPTY_LIST);
        }
        return categoryMsgConfigResultDTO;
    }

    @Override
    public boolean saveOrUpdateConfig(SaveCategoryPhoneConfigDTO saveCategoryPhoneConfigDTO) {
        YdCategoryPhoneConfig ydCategoryPhoneConfig = new YdCategoryPhoneConfig();
        ydCategoryPhoneConfig.setId(saveCategoryPhoneConfigDTO.getId());
        ydCategoryPhoneConfig.setLineName(saveCategoryPhoneConfigDTO.getLineName());
        ydCategoryPhoneConfig.setLineCode(saveCategoryPhoneConfigDTO.getLineCode());
        ydCategoryPhoneConfig.setPhones(saveCategoryPhoneConfigDTO.getPhones());
        ydCategoryPhoneConfig.setPushMsgFlag(saveCategoryPhoneConfigDTO.getPushMsgFlag());
        ydCategoryPhoneConfig.setFrequency(saveCategoryPhoneConfigDTO.getFrequency());
        ydCategoryPhoneConfig.setCutOff(saveCategoryPhoneConfigDTO.getCutOff());
        ydCategoryPhoneConfig.setGrade(saveCategoryPhoneConfigDTO.getGrade());
        ydCategoryPhoneConfig.setComponent(saveCategoryPhoneConfigDTO.getComponent());
        if (Objects.nonNull(ydCategoryPhoneConfig.getId())){
            this.updateById(ydCategoryPhoneConfig);
        }else {
            this.save(ydCategoryPhoneConfig);
        }
        return true;
    }

    @Override
    public boolean deleteConfig(Integer id) {
        UpdateWrapper<YdCategoryPhoneConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(YdCategoryPhoneConfig::getDelFlag,1)
                .eq(YdCategoryPhoneConfig::getId,id);
        this.update(updateWrapper);
        return true;
    }

    private List<String> getGradeOrComponents(String grades, List<YdFaultGradeConfig> gradeConfigList) {
        List<String> gradeCodeList = Arrays.asList(grades.split(","));
        if (!CollectionUtils.isEmpty(gradeCodeList)) {
            Map<String, String> gradeMap = gradeConfigList.stream().collect(Collectors.toMap(YdFaultGradeConfig::getCodeValue, YdFaultGradeConfig::getShowName));
            return gradeCodeList.stream().map(grade -> {
                String gradeName = gradeMap.get(grade);
                return gradeName;
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }
}
