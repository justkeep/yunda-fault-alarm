package com.yunda.faultalarm.configure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.yunda.faultalarm.dal.mapper")
public class MybatisConfigure {
}
