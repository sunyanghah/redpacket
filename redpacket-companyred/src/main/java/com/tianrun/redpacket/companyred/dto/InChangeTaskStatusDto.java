package com.tianrun.redpacket.companyred.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by dell on 2019/1/16.
 * @author dell
 */
@Data
public class InChangeTaskStatusDto {

    /**
     * 任务是否基于次数，比如关注公众号，张三第一次关注了，第二个红包活动又选了此任务。张三是不是算已经完成
     * 如果算的话，像是填表单之类的，每次都不一样。该怎么处理。
     */
    @NotBlank
    private String userAccount;

    @NotBlank
    private String taskCode;

    @NotBlank
    private String taskStatus;

    @NotBlank
    private String secureKey;
}
