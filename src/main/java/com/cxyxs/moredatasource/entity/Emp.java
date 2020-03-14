package com.cxyxs.moredatasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.omg.CORBA.IDLType;

/**
 * Description：
 * Author:  程序猿学社
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
