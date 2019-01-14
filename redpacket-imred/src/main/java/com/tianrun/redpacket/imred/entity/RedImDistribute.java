package com.tianrun.redpacket.imred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Data
@TableName("tb_red_im_distribute")
public class RedImDistribute {

    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 红包编号
     */
    @TableField("red_no")
    private String redNo;

    /**
     * 发送红包人的账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 红包类型(凭手气，普通)
     */
    @TableField("red_type")
    private String redType;

    /**
     * 红包个数
     */
    @TableField("red_num")
    private Integer redNum;

    /**
     * 红包总金额
     */
    @TableField("red_money")
    private Integer redMoney;

    /**
     * 红包单个金额，普通红包时设置
     */
    @TableField("red_price")
    private Integer redPrice;

    /**
     * 红包派发时间
     */
    @TableField("send_time")
    private Date sendTime;

    /**
     * 红包过期时间
     */
    @TableField("deadline_time")
    private Date deadlineTime;

    /**
     * 红包面向对象(群聊或单聊)
     */
    @TableField("red_object")
    private String redObject;

    /**
     * 对象id(群组id或个人账号)
     */
    @TableField("object_account")
    private String objectAccount;

    /**
     * 对象名称(群组名或个人名)
     */
    @TableField("object_name")
    private String objectName;
}
