package com.tianrun.redpacket.companyred.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by dell on 2019/1/8.
 * @author dell
 */
@Data
public class InUpdateTaskDto {

    @NotNull
    private Long id;

    @NotBlank
    private String taskCode;

    @NotBlank
    private String taskName;

    private String taskDesc;

    @NotNull
    private Date startTime;

    @NotNull
    private Date deadlineTime;

    @NotBlank
    private String secureKey;

    @NotBlank
    private String finishedCode;

    private String remarks;

    @NotBlank
    private String accessFlag;

    @NotBlank
    private String usefulFlag;
}
