package com.cxyxs.moredatasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description：V2.0版本的代码
 * Author: 程序猿学社
 * Date:  2020/3/14 1:20
 * Modified By:
 */
@ConfigurationProperties(prefix="spring.datasource.test1")
@Data
public class Test1Config {
    private String url;
    private String username;
    private String password;
}
