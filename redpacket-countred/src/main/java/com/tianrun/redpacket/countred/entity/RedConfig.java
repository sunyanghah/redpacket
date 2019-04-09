package com.tianrun.redpacket.countred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@TableName("tb_red_config")
@Data
public class RedConfig {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    @TableField("config_code")
    private String configCode;

    @TableField("config_value")
    private String configValue;

    @TableField("config_desc")
    private String configDesc;

    @TableField("update_user")
    private String updateUser;

    @TableField("update_time")
    private Date updateTime;

    @TableField("remarks")
    private String remarks;
}
