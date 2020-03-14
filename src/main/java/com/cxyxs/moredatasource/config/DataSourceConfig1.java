/*
package com.cxyxs.moredatasource.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
@MapperScan(basePackages= {"com.cxyxs.moredatasource.test1.dao"},sqlSessionFactoryRef="test1SqlSessionFactory")
public class DataSourceConfig1 {
    @Bean(name="test1DataSource")
    @ConfigurationProperties(prefix="spring.datasource.test1")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="test1SqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("test1DataSource") DataSource dataSource,@Qualifier("testPaginationInterceptor") PaginationInterceptor paginationInterceptor) throws Exception {
        //MP的工厂
        MybatisSqlSessionFactoryBean bean=new MybatisSqlSessionFactoryBean ();
        //mybatis的工厂
        //SqlSessionFactory bean=new MybatisSqlSessionFactoryBean ();

        bean.setDataSource(dataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/com/cxyxs/moredatasource/test1/mapper/*.xml");
        bean.setMapperLocations(resources);
        Interceptor[] plugins = new Interceptor[]{paginationInterceptor};
        bean.setPlugins(plugins);
        return bean.getObject();
    }

    @Bean(name="test1TransactionManager")//配置事务
    public DataSourceTransactionManager testTransactionManager(@Qualifier("test1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("testPaginationInterceptor")
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }


    @Bean(name="test1SqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("test1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
       return new SqlSessionTemplate(sqlSessionFactory);
    }
}
*/
