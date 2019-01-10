package com.tianrun.redpacket.countred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@TableName("tb_red_freeze")
@Data
public class RedFreeze {

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    /**
     * 被冻结人员的账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 是否冻结im抢红包
     */
    @TableField("freeze_im_grab")
    private String freezeImGrab;

    /**
     * 是否冻结im发红包
     */
    @TableField("freeze_im_send")
    private String freezeImSend;

    /**
     * 是否冻结抢企业红包
     */
    @TableField("freeze_activity_grab")
    private String freezeActivityGrab;
}
