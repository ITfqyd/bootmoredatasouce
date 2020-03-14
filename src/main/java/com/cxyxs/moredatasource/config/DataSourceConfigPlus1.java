package com.cxyxs.moredatasource.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
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
@MapperScan(basePackages= {"com.cxyxs.moredatasource.test1.dao"},sqlSessionFactoryRef="test1SqlSessionFactory")
public class DataSourceConfigPlus1 {

	// 配置数据源
	@Bean("test1DataSource")
	public DataSource testDataSource (Test1Config testConfig) throws SQLException {
		//表示使用的是mysql数据库
		MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
		mysqlXaDataSource.setUrl(testConfig.getUrl());
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		mysqlXaDataSource.setPassword(testConfig.getPassword());
		mysqlXaDataSource.setUser(testConfig.getUsername());
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);

		//Atomikos负责管理所有的事务1
		AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		xaDataSource.setXaDataSource(mysqlXaDataSource);
		xaDataSource.setUniqueResourceName("test1DataSource");
		return xaDataSource;
	}

	@Bean(name = "test1SqlSessionFactory")
	public SqlSessionFactory testSqlSessionFactory(@Qualifier("test1DataSource") DataSource dataSource,@Qualifier("test1PaginationInterceptor") PaginationInterceptor paginationInterceptor)
			throws Exception {
		//注意，这里引入的事MP的工厂,而不是mybatis的工厂SqlSessionFactoryBean
		MybatisSqlSessionFactoryBean bean=new MybatisSqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		//引入Mapper.xml文件的位置
		Resource[] resources = new PathMatchingResourcePatternResolver()
				.getResources("classpath*:/com/cxyxs/moredatasource/test1/mapper/*.xml");
		bean.setMapperLocations(resources);

		//保证MP的分页插件可用
		Interceptor[] plugins = new Interceptor[]{paginationInterceptor};
		bean.setPlugins(plugins);
		return bean.getObject();
	}

	/**
	 * 分页插件
	 * @return
	 */
	@Bean("test1PaginationInterceptor")
	public PaginationInterceptor paginationInterceptor(){
		return new PaginationInterceptor();
	}

	@Bean(name = "test1SqlSessionTemplate")
	public SqlSessionTemplate testSqlSessionTemplate(
			@Qualifier("test1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}