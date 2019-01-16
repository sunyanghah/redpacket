package com.tianrun.redpacket.companyred.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2019/1/8.
 * @author dell
 */
@Data
public class InAddActivityDto {

    private Long id;

    /**
     * 活动类型(企业现金，通用场景，指定场景)
     */
    @NotBlank
    private String activityType;

    /**
     * 指定的场景(指定场景活动时)
     */
    private List<ActivityPlaceDto> placeList;

    /**
     * 活动名称
     */
    @NotBlank
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     *  开始时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date endTime;

    /**
     * 红包类型(凭手气,普通)
     */
    @NotNull
    private String redType;

    /**
     * 红包个数
     */
    @NotNull
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
     * 单笔最大领取金额
     */
    private Integer maxPrice;

    /**
     * 红包祝福语
     */
    private String redBless;

    /**
     * 红包发放说明
     */
    private String redRemark;

    /**
     * 领取人员范围
     */
    private InRedAuthDto userAuth;

    /**
     * 前置任务
     */
    private List<Long> taskIds;
}
