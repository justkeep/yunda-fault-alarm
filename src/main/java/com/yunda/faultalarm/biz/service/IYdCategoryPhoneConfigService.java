package com.yunda.faultalarm.biz.service;

import com.yunda.faultalarm.biz.dto.CategoryMsgConfigResultDTO;
import com.yunda.faultalarm.biz.dto.QueryConfigParams;
import com.yunda.faultalarm.biz.dto.SaveCategoryPhoneConfigDTO;
import com.yunda.faultalarm.dal.model.YdCategoryPhoneConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
public interface IYdCategoryPhoneConfigService extends IService<YdCategoryPhoneConfig> {

    /**
     * 分页获取配置信息
     * @param queryConfigParams
     */
    CategoryMsgConfigResultDTO queryCategoryConfigPageList(QueryConfigParams queryConfigParams);

    /**
     * 保存或者更新配置信息
     * @param saveCategoryPhoneConfigDTO
     * @return
     */
    boolean saveOrUpdateConfig(SaveCategoryPhoneConfigDTO saveCategoryPhoneConfigDTO);

    /**
     * 根据主键Id删除配置
     * @param id
     * @return
     */
    boolean deleteConfig(Integer id);

}
