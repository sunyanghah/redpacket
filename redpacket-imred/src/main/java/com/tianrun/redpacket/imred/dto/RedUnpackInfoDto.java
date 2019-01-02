package com.tianrun.redpacket.imred.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2018/12/24.
 * @author dell
 */
@Data
public class RedUnpackInfoDto {

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 拆包时间
     */
    private Date unpackTime;

    /**
     * 拆得金额
     */
    private Integer unpackMoney;

    /**
     * 是否运气王 true 是  false 否
     */
    private boolean bestLuck;
}
