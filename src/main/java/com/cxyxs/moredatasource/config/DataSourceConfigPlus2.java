package com.cxyxs.moredatasource.config;
 
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(basePackages= {"com.cxyxs.moredatasource.test2.dao"},sqlSessionFactoryRef="test2SqlSessionFactory")
public class DataSourceConfigPlus2 {
 
	// 配置数据源
	@Bean("test2DataSource")
	public DataSource testDataSource (Test2Config testConfig) throws SQLException {
		MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
		mysqlXaDataSource.setUrl(testConfig.getUrl());
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		mysqlXaDataSource.setPassword(testConfig.getPassword());
		mysqlXaDataSource.setUser(testConfig.getUsername());
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
 
		AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		xaDataSource.setXaDataSource(mysqlXaDataSource);
		xaDataSource.setUniqueResourceName("test2DataSource");
		return xaDataSource;
	}
 
	@Bean(name = "test2SqlSessionFactory")
	public SqlSessionFactory testSqlSessionFactory(@Qualifier("test2DataSource") DataSource dataSource,@Qualifier("test2PaginationInterceptor") PaginationInterceptor paginationInterceptor)
			throws Exception {
		MybatisSqlSessionFactoryBean bean=new MybatisSqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		Resource[] resources = new PathMatchingResourcePatternResolver()
				.getResources("classpath*:/com/cxyxs/moredatasource/test2/mapper/*.xml");
		bean.setMapperLocations(resources);
		Interceptor[] plugins = new Interceptor[]{paginationInterceptor};
		bean.setPlugins(plugins);
		return bean.getObject();
	}

	@Bean("test2PaginationInterceptor")
	public PaginationInterceptor paginationInterceptor(){
		return new PaginationInterceptor();
	}

	@Bean(name = "test2SqlSessionTemplate")
	public SqlSessionTemplate testSqlSessionTemplate(
			@Qualifier("test2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}