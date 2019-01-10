package com.tianrun.redpacket.companyred.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/1/8.
 * @author dell
 */
@Data
public class OutQueryTaskDto {

    private Long id;

    private String taskCode;

    private String taskName;

    private String taskDesc;

    private Date startTime;

    private Date deadlineTime;

    private String secureKey;

    private String finishedCode;

    private String remarks;

    private String accessFlag;

    private String usefulFlag;
}
