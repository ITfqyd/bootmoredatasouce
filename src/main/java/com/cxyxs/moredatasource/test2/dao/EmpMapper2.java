package com.cxyxs.moredatasource.test2.dao;

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
public interface EmpMapper2  extends  BaseMapper<Emp>{
    @Select("select * from emp")
    public List<Emp> selectList();

    /**
     * 测试mapper
     * @return
     */
    public List<Emp> getAll();

    public List<Emp> getAll1();
}
