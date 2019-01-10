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
@TableName("tb_red_refund")
public class RedRefund {

    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * IM红包id
     */
    @TableField("red_id")
    private Long redId;

    /**
     * 退款方式
     */
    @TableField("refund_way")
    private String refundWay;

    /**
     * 退回金额
     */
    @TableField("refund_money")
    private Integer refundMoney;

    /**
     * 退回时间
     */
    @TableField("refund_time")
    private Date refundTime;

    /**
     * 退回原因
     */
    @TableField("refund_reason")
    private String refundReason;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}
