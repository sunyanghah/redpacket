package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tianrun.redpacket.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Data
@TableName("tb_red_activity")
public class RedActivity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    /**
     * 红包活动名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     *  开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 活动状态
     */
    @TableField("activity_status")
    private String activityStatus;

    /**
     * 活动类型(企业现金，通用场景，指定场景)
     */
    @TableField("activity_type")
    private String activityType;

    /**
     * 企业Code(指定场景时指定企业)
     */
    @TableField("client_code")
    private String clientCode;

    /**
     * 企业名称(指定场景时指定)
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 红包类型(凭手气,普通)
     */
    @TableField("red_type")
    private String redType;

    /**
     * 单笔最大领取金额
     */
    @TableField("max_price")
    private Integer maxPrice;

    /**
     * 红包金额过期时间
     */
    @TableField("deadline_time")
    private Date deadlineTime;

    /**
     * 红包个数
     */
    @TableField("red_num")
    private Integer redNum;

    /**
     * 红包单个金额(普通红包下设置)
     */
    @TableField("red_price")
    private Integer redPrice;

    /**
     * 红包总金额
     */
    @TableField("red_amount")
    private Integer redAmount;

    /**
     * 红包祝福语
     */
    @TableField("red_bless")
    private String redBless;

    /**
     * 红包发放说明
     */
    @TableField("red_remark")
    private String redRemark;

    /**
     * 盐
     */
    @TableField("salt")
    private String salt;
}
