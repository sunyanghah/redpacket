package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@TableName("tb_red_authorization")
@Data
public class RedAuthorization {

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    /**
     * 红包id
     */
    @TableField("red_id")
    private Long redId;

    /**
     * 用户账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 是否可以抢红包，0可以，1不可以
     */
    @TableField("can_grab_flag")
    private String canGrabFlag;

}
