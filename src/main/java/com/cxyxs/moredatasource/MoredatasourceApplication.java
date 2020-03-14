package com.cxyxs.moredatasource;

import com.cxyxs.moredatasource.config.Test1Config;
import com.cxyxs.moredatasource.config.Test2Config;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cxyxs"})
@EnableSwagger2Doc
@EnableConfigurationProperties(value = { Test1Config.class, Test2Config.class })
public class MoredatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoredatasourceApplication.class, args);
    }

}
