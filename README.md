>程序猿学社的GitHub，欢迎**Star**
[https://github.com/ITfqyd/cxyxs](https://github.com/ITfqyd/cxyxs)
本文已记录到github，形成对应专题。

# 前言
>前一篇文章，我们已经实现了通过**springboot+MP实现多数据源**，实际上一章的代码，如果一个方法中操作多个数据源，如果中间出现异常，可能会存在一个**入库成功**，另外一个**入库失败**的问题。而没有保证**多个数据源事务的一致性**。、

- 为了减少重复的代码，在阅读本文章之前，请先搭建好springboot集成多数据源环境。也是为了减少重复的代码。
[【springboot专题】二十 springboot中如何配置多数据源](https://blog.csdn.net/qq_16855077/article/details/104708038)
# 事务不一致问题
继续在TestController类上加上如下代码，两个库，为了方便，我们就记录为test1和test2
```java
 @ApiOperation("同时向test1和test2中插入数据,增加指定某个事务的代码,并故意在代码中报错")
    @PostMapping("/saveEmp4")
    @Transactional(value = "test2TransactionManager")
    public String saveEmp4(Emp emp) {
        int insert = empMapper1.insert(emp);
          insert = empMapper2.insert(emp);
        //故意报错
        String str= null;
        System.out.println(str.toString());   //这里会报错
        if(insert > 0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    };
```
输入**http://localhost:8080/swagger-ui.html**
![](https://img-blog.csdnimg.cn/20200314002546325.png)
点击**try it out** 运行。
给一个小提示，注意看清楚，配置的事务是**test2TransactionManager**

**大家觉得test1和test2这两个库的emp表，会有什么变化？**
有不少社友的答案，是不是test1和test2都不插入数据。

**话不多说，检查一波数据库的身体**
![](https://img-blog.csdnimg.cn/20200314003633920.png)
![](https://img-blog.csdnimg.cn/20200314003658162.png)
- 通过查看数据库，可以发现test1数据源对应的库插入成功一条“社长是个大帅哥”的记录。 test2数据源对应的库，表没有变化。
- 分析现象，可以发现，test2数据源对应的库，没有变化成功，就是因为我们在方法上配置test2TransactionManager的事务，说明只有test2才会存在事务，test1实际上是没有事务的。
- 通过查看@Transactional注解的源码，alt+7(idea版本查询类所有方法)，通过仔细检查，发现没有一个字段能自定多个数据源。那怎么办？是不是就没有别的办法，可以解决多数据源事务的问题，别急。
![](https://img-blog.csdnimg.cn/20200314004439670.png)
- 一般多数据源中，解决事务问题，采用**jta+atomikos**
# jta+atomikos
![](https://img-blog.csdnimg.cn/20200314022959755.png)
## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.5.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.cloudtech</groupId>
    <artifactId>moredatasource</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>moredatasource</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!--web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- 增加thymeleaf坐标  -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!--简化实体类-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 热部署 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>

        <!--swagger2-->
        <dependency>
            <groupId>com.spring4all</groupId>
            <artifactId>spring-boot-starter-swagger</artifactId>
            <version>1.5.1.RELEASE</version>
        </dependency>

        <!--MP插件，简化sql操作-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.3.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.18</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.6</version>
        </dependency>

        <!--swagger2-->
        <dependency>
            <groupId>com.spring4all</groupId>
            <artifactId>spring-boot-starter-swagger</artifactId>
            <version>1.5.1.RELEASE</version>
        </dependency>

        <!--jta+atomikos依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jta-atomikos</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <build>
        <!--解决编译后，xml文件没有过去的问题-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>include</include>
                </includes>
            </resource>
        </resources>

        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```
- 跟上个版本的pom.xml相比，只是多引入一个 jta+atomikos依赖。

## application.yml
```yml
server:
    port: 8888
spring:
  datasource:
    test1:
      url: jdbc:mysql://localhost:3306/pro?useUnicode=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8
      username: root
      password: root

    test2:
      url: jdbc:mysql://localhost:3306/pro1?useUnicode=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8
      username: root
      password: root



mybatis-plus:
  configuration:
    ##打印sql日志,本地测试使用，生产环境不要使用，注意、注意、注意
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

####扫描swagger注解
swagger:
  base-package: com.cxyxs
```
-  跟上个版本代码相比，代码有变动。

## 实体类
```java
package com.cxyxs.moredatasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.omg.CORBA.IDLType;

/**
 * Description：
 * Author: 程序猿学社
 * Date:  2020/3/7 12:03
 * Modified By:
 */
@Data
public class Emp {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer age;
}
```
## dao类
```java
package com.cxyxs.moredatasource.test1.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cxyxs.moredatasource.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description：
 * Author: 程序猿学社
 * Date:  2020/3/7 12:01
 * Modified By:
 */
@Repository
public interface EmpMapper1 extends  BaseMapper<Emp>{
    @Select("select * from emp")
    public List<Emp> selectList();
    /**
     * 测试mapper
     * @return
     */
    public List<Emp> getAll();
}
```
## mapper.xml
<?xml version="1.0" encoding="UTF-8"?>

```xml
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.cxyxs.moredatasource.test1.dao.EmpMapper1">
  
  <!--  根据区域名称获取区域代码-->
   <select id="getAll" resultType="com.cxyxs.moredatasource.entity.Emp">
		select * from emp
  </select>
</mapper>
```
- 注意：因为test2跟test1包下的dao和mapper都是类似的，只是把test1或者EmpMapper1改为test2和EmpMapper2就可以，这里就不贴一些重复代码。

## 读取数据库配置
### Test1Config 
```java
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
```
- @ConfigurationProperties读取以对应配置为前缀的值
- @Data使用该注解，可以不用set get
- 发现有一个报错提示，别慌，按提示配置一下就行，需要在application启动类里面**增加EnableConfigurationProperties注解**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200314023756458.png)
### Test2Config 
```java
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
@ConfigurationProperties(prefix="spring.datasource.test2")
@Data
public class Test2Config {
    private String url;
    private String username;
    private String password;
}
```
## 启动类

```java
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
```
- @ConfigurationProperties注解一般是需要跟@Componen配套使用的，如果没有指定，还有一种方式，就是@EnableConfigurationPropertie表示注入。
## 数据库配置
### DataSourceConfigPlus1

```java
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

		//Atomikos负责管理所有的事务
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
```
### DataSourceConfigPlus2

```java
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
```
## controller
```java
package com.cxyxs.moredatasource.controller;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.cxyxs.moredatasource.entity.Emp;
import com.cxyxs.moredatasource.test1.dao.EmpMapper1;
import com.cxyxs.moredatasource.test2.dao.EmpMapper2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Description：
 * Author: 程序猿学社
 * Date:  2020/3/7 12:15
 * Modified By:
 */
@RestController
@Api("测试多数据源接口")
public class TestController {
    @Autowired
    private EmpMapper1 empMapper1;
    @Autowired
    private EmpMapper2 empMapper2;

    @ApiOperation("测试mybatis@select注解，通过test1数据库实现")
    @GetMapping("/getKing1")
    public List getKing1(){
        List<Emp> emps = empMapper1.selectList();
        return emps;
    };


    @ApiOperation("测试mybatis@select注解，通过test2数据库实现")
    @GetMapping("/getKing2")
    public List getKing2(){
        List<Emp> emps = empMapper2.selectList();
        return emps;
    };

    @ApiOperation("测试mybatis的mapper.xml文件调用，通过test1数据库实现")
    @GetMapping("/getKing3")
    public List getKing3(){
        List<Emp> emps = empMapper1.getAll();
        return emps;
    };

    @ApiOperation("测试mybatis的mapper.xml文件调用，通过test2数据库实现")
    @GetMapping("/getKing4")
    public List getKing4(){
        List<Emp> emps = empMapper2.getAll();
        return emps;
    };

    @ApiOperation("通过mp调用test1数据库实现查询")
    @GetMapping("/getKing5")
    public List getKing5(){
        List<Emp> emps = empMapper1.selectList(null);
        return emps;
    };

    @ApiOperation("通过mp调用test2数据库实现查询")
    @GetMapping("/getKing6")
    public List getKing6(){
        List<Emp> emps = empMapper2.selectList(null);
        return emps;
    };

    @ApiOperation("测试插入数据")
    @PostMapping("/saveEmp1")
    @Transactional
    public String saveEmp(Emp emp) {
        int insert = empMapper1.insert(emp);
        if(insert > 0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    };

    @ApiOperation("测试给test1插入数据,增加指定某个事务的代码")
    @PostMapping("/saveEmp2")
    @Transactional(value = "test1TransactionManager")
    public String saveEmp2(Emp emp) {
        int insert = empMapper1.insert(emp);
        if(insert > 0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    };

    @ApiOperation("测试给test1插入数据,增加指定某个事务的代码,并故意在代码中报错")
    @PostMapping("/saveEmp3")
    @Transactional(value = "test1TransactionManager")
    public String saveEmp3(Emp emp) {
        int insert = empMapper1.insert(emp);
        //故意报错
        String str= null;
        System.out.println(str.toString());   //这里会报错
        if(insert > 0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    };

    @ApiOperation("同时向test1和test2中插入数据,增加指定某个事务的代码,并故意在代码中报错")
    @PostMapping("/saveEmp4")
    @Transactional
    public String saveEmp4(Emp emp) {
        int insert = empMapper1.insert(emp);
        insert = empMapper2.insert(emp);
        //故意报错
        String str= null;
        System.out.println(str.toString());   //这里会报错
        if(insert > 0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    };

    @ApiOperation("同时向test1和test2中插入数据,增加指定某个事务的代码,不故意报错")
    @PostMapping("/saveEmp5")
    @Transactional
    public String saveEmp5(Emp emp) {
        int insert = empMapper1.insert(emp);
        insert = empMapper2.insert(emp);
        if(insert > 0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    };
}
```
- 只需要关注saveEmp4和saveEmp5方法
### saveEmp4方法，同时操作多个数据源，故意报错
![](https://img-blog.csdnimg.cn/20200314025520423.png)
**各位社友，猜猜两个库的变化？**
- 直接公布答案，就不检查数据库的身体，两个库，都插入失败，说明事务生效。
### saveEmp5方法，同时操作多个数据源，不报错
![](https://img-blog.csdnimg.cn/20200314025910934.png)
**各位社友，猜猜两个库的变化？**
- 两个库都插入生成。
#  浅谈jta+atomikos
![](https://img-blog.csdnimg.cn/20200314132050512.png)
**优点：**
- 通过jta+atomikos可以解决多数据源事务的问题，底层原理就是把多个事务，交给atomikos管理，这样就可以帮我们解决@Transactional注解只能指定一个数据源的问题。

**缺点：**
- 需要拿到数据源才能通过atomikos来管理事务，在实际开发过程中，这点几乎很难实现，所以一般在小项目中使用。在分布式场景中，几乎不采用这种方式。

**关注公众号"程序猿学社"，回复关键字999，获取源码**
---
觉得不错，可以帮我**点个赞**，让更多的人看到。
更多好看的内容，可以查看我的[github专题](https://github.com/ITfqyd/cxyxs)，各个技术点会形成对应的专题。
