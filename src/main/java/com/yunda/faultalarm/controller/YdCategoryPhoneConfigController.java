package com.yunda.faultalarm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunda.faultalarm.biz.dto.*;
import com.yunda.faultalarm.biz.service.IYdCategoryPhoneConfigService;
import com.yunda.faultalarm.biz.service.IYdFaultGradeConfigService;
import com.yunda.faultalarm.dal.model.YdFaultGradeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 页面接口
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class YdCategoryPhoneConfigController {

    @Autowired
    private IYdCategoryPhoneConfigService categoryPhoneConfigService;
    @Autowired
    private IYdFaultGradeConfigService gradeConfigService;
    @Autowired
    private ApplicationContext context;

    /**
     * 配置列表
     *
     * @param queryConfigParams
     * @return
     */
    @PostMapping("/query_config_by_page")
    public BaseResponse queryCategoryConfigPageList(QueryConfigParams queryConfigParams) {
        CategoryMsgConfigResultDTO categoryMsgConfigResultDTO = categoryPhoneConfigService.queryCategoryConfigPageList(queryConfigParams);
        return BaseResponse.buildSuccess(categoryMsgConfigResultDTO);
    }

    /**
     * 保存或者更新配置信息
     *
     * @param saveCategoryPhoneConfigParams
     * @return
     */
    @PostMapping("/save_or_update_config")
    public BaseResponse saveOrUpdateConfig(@Valid SaveCategoryPhoneConfigDTO saveCategoryPhoneConfigParams) {
        categoryPhoneConfigService.saveOrUpdateConfig(saveCategoryPhoneConfigParams);
        return BaseResponse.buildSuccess();
    }

    /**
     * 保存或者更新配置信息
     *
     * @param id
     * @return
     */
    @GetMapping("/delete_config")
    public BaseResponse deleteConfig(@RequestParam("id") Integer id) {
        categoryPhoneConfigService.deleteConfig(id);
        return BaseResponse.buildSuccess();
    }

    /**
     * 查询故障等级和部件信息
     *
     * @return
     */
    @GetMapping("/query_grade_and_component")
    public BaseResponse<GradeAndComponentResultDTO> queryGradeAndComponent() {
        QueryWrapper<YdFaultGradeConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(YdFaultGradeConfig::getCategory, Arrays.asList("grade", "component"));
        List<YdFaultGradeConfig> gradeConfigList = gradeConfigService.list(queryWrapper);
        Map<String, List<YdFaultGradeConfig>> groupCategory = gradeConfigList.stream().collect(Collectors.groupingBy(YdFaultGradeConfig::getCategory));
        GradeAndComponentResultDTO gradeAndComponentResultDTO = new GradeAndComponentResultDTO();
        groupCategory.forEach((k, v) -> {
            List<GradeAndComponentResultDTO.Detail> collect = v.stream().map(data -> {
                GradeAndComponentResultDTO.Detail detail = new GradeAndComponentResultDTO.Detail();
                detail.setCodeValue(data.getCodeValue());
                detail.setShowName(data.getShowName());
                detail.setGroupNumber(data.getGroupNumber());
                return detail;
            }).collect(Collectors.toList());
            if ("grade".equals(k)) {
                gradeAndComponentResultDTO.setGrades(collect);
            }
            if ("component".equals(k)) {
                gradeAndComponentResultDTO.setComponents(collect);
            }
        });
        HttpMessageConverters converters = context.getBean(HttpMessageConverters.class);
        return BaseResponse.buildSuccess(gradeAndComponentResultDTO);
    }

}
