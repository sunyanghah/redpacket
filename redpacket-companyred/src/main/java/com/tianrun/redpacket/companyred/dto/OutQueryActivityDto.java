package com.tianrun.redpacket.companyred.dto;

import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.dict.DictValueHandle;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Data
public class OutQueryActivityDto {

    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动类型
     */
    @DictValueHandle(dictType = DictConstant.ACTIVITY_TYPE)
    private String activityType;

    /**
     * 红包类型
     */
    @DictValueHandle(dictType = DictConstant.RED_TYPE)
    private String redType;

    /**
     * 红包总金额
     */
    private Integer redAmount;

    /**
     * 活动状态
     */
    @DictValueHandle(dictType = DictConstant.ACTIVITY_STATUS)
    private String activityStatus;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;
}
