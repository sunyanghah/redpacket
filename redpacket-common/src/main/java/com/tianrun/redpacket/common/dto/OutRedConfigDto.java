package com.tianrun.redpacket.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/4/9.
 * @author dell
 */
@Data
public class OutRedConfigDto {

    private Long id;

    private String configCode;

    private String configValue;

    private String configDesc;

    private String remarks;

    private String updateUser;

    private Date updateTime;

}
