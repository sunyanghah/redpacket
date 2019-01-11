package com.tianrun.redpacket.companyred.dto;

import com.tianrun.redpacket.common.dto.InBasePageDto;
import lombok.Data;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Data
public class InQueryActivityDto extends InBasePageDto {

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 红包类型
     */
    private String redType;

    /**
     * 红包金额起始条件
     */
    private Integer startRedAmount;

    /**
     * 红包金额终止条件
     */
    private Integer endRedAmount;

    /**
     * 红包活动名称，模糊查询
     */
    private String name;

    /**
     * 活动状态
     */
    private String activityStatus;
}
