package com.tianrun.redpacket.companyred.dto;

import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.platform.DictValueHandle;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Data
public class OutGetActivityDto {

    /**
     * 主键
     */
    private Long id;

    /**
     * 红包活动名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     *  开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 活动是否发布
     */
    @DictValueHandle(dictType = DictConstant.YES_NO)
    private String releaseFlag;

    /**
     * 活动发布时间
     */
    private Date releaseTime;

    /**
     * 活动类型(企业现金，通用场景，指定场景)
     */
    @DictValueHandle(dictType = DictConstant.ACTIVITY_TYPE)
    private String activityType;

    /**
     * 企业Code(指定场景时指定企业)
     */
    private String clientCode;

    /**
     * 企业名称(指定场景时指定)
     */
    private String clientName;

    /**
     * 红包类型(凭手气,普通)
     */
    @DictValueHandle(dictType = DictConstant.RED_TYPE)
    private String redType;

    /**
     * 单笔最大领取金额
     */
    private Integer maxPrice;

    /**
     * 红包金额过期时间
     */
    private Date deadlineTime;

    /**
     * 红包个数
     */
    private Integer redNum;

    /**
     * 红包单个金额(普通红包下设置)
     */
    private Integer redPrice;

    /**
     * 红包总金额
     */
    private Integer redAmount;

    /**
     * 红包祝福语
     */
    private String redBless;

    /**
     * 红包发放说明
     */
    private String redRemark;

    /**
     * 盐
     */
    private String salt;

    /**
     * 支付方式
     */
    private String payWay;

    /**
     * 领取人员范围
     */
    private InRedAuthDto userAuth;

    /**
     * 前置任务
     */
    private List<Long> taskIds;

}
