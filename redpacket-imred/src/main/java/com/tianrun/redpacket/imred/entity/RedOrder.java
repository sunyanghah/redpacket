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
@TableName("tb_red_order")
@Data
public class RedOrder {

    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 红包订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 红包总金额
     */
    @TableField("red_money")
    private Integer redMoney;

    /**
     * 订单创建人账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 红包类型(拼手气，普通)
     */
    @TableField("red_type")
    private String redType;

    /**
     * 红包个数
     */
    @TableField("red_num")
    private Integer redNum;

    /**
     * 红包祝福语
     */
    @TableField("red_content")
    private String redContent;

    /**
     * 订单创建时间
     */
    @TableField("distribute_time")
    private Date distributeTime;

    /**
     * 红包面向对象(群聊或单聊)
     */
    @TableField("red_object")
    private String redObject;

    /**
     * 订单状态
     */
    @TableField("status")
    private String status;

    /**
     * 支付方式
     */
    @TableField("pay_way")
    private String payWay;

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

    /**
     * 盐
     */
    @TableField("salt")
    private String salt;

}
