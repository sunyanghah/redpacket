package com.tianrun.redpacket.imred.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/1/14.
 * @author dell
 */
@Data
public class UnpackMessageDto {

    /**
     * 红包编号
     */
    private String redNo;

    /**
     * 抢了多少钱
     */
    private Integer money;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * im还是企业红包
     */
    private String redSource;

    /**
     * 抢红包时间
     */
    private Date grabTime;

    /**
     * 是否是最后一个红包
     */
    private boolean isLastOne;

}
