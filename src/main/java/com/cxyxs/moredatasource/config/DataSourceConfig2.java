/*
package com.cxyxs.moredatasource.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

*/
/**
 * Description：v1.0版本的代码
 * Author: 程序猿学社
 * Date:  2020/3/7 18:14
 * Modified By:
 *//*

@Configuration
@MapperScan(basePackages= {"com.cxyxs.moredatasource.test2.dao"},sqlSessionFactoryRef="test2SqlSessionFactory")
public class DataSourceConfig2 {
    @Bean(name="test2DataSource")
    @ConfigurationProperties(prefix="spring.datasource.test2")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="test2SqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("test2DataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean=new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
       Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/com/cxyxs/moredatasource/test2/mapper/*.xml");
        bean.setMapperLocations(resources);
        return bean.getObject();
    }

    @Bean(name="test2TransactionManager")//配置事务
    public DataSourceTransactionManager testTransactionManager(@Qualifier("test2DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name="test2SqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("test2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
       return new SqlSessionTemplate(sqlSessionFactory);
    }
}
*/
