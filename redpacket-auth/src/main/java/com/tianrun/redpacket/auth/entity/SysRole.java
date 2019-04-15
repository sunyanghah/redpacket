package com.tianrun.redpacket.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2018/10/31.
 * @author dell
 */
@TableName("sys_role")
@Data
public class SysRole {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String name;
}
