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
