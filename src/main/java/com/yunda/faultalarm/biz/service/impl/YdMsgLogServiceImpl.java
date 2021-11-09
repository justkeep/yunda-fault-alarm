package com.yunda.faultalarm.biz.service.impl;

import com.yunda.faultalarm.dal.model.YdMsgLog;
import com.yunda.faultalarm.dal.mapper.YdMsgLogMapper;
import com.yunda.faultalarm.biz.service.IYdMsgLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 短信发送日志记录表 服务实现类
 * </p>
 *
 * @author GYK
 * @since 2021-11-09
 */
@Service
public class YdMsgLogServiceImpl extends ServiceImpl<YdMsgLogMapper, YdMsgLog> implements IYdMsgLogService {

}
