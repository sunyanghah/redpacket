package com.tianrun.redpacket.companyred.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Data
public class InAddTaskDto {

    /**
     * 任务code
     */
    @NotBlank
    private String taskCode;

    /**
     * 任务名称
     */
    @NotBlank
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 任务开始时间 （字段待定，任务需不需要开始时间与结束时间。
     * 需要有的话，怎么处理，什么逻辑）
     */
    @NotNull
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 任务结束时间（字段待定，任务需不需要开始时间与结束时间。
     * 需要有的话，怎么处理，什么逻辑）
     */
    @NotNull
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadlineTime;

    /**
     * 安全key，修改完成状态的时候需要携带，
     * 防止用户恶意修改
     */
    @NotBlank
    private String secureKey;

    /**
     * 完成状态值。修改完成状态时，
     * 如果完成状态等于此值。则视为完成
     */
    @NotBlank
    private String finishedCode;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 访问状态 失效时
     * 第三方应用将不可对此任务ID下的用户状态进行修改，仍可查询
     */
    @NotBlank
    private String accessFlag;

    /**
     * 领取状态 失效时
     * 使用此前置条件的红包将不可被用户领取。
     */
    @NotBlank
    private String usefulFlag;



}
