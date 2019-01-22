package com.tianrun.redpacket.companyred.dto;

import lombok.Data;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
@Data
public class OutUnpackDto {

    private boolean canUnpackFlag;

    private String msg;

    /**
     * 抢了多少钱，单位分
     */
    private Integer money;
}
