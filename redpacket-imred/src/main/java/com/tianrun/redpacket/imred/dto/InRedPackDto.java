package com.tianrun.redpacket.imred.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Data
public class InRedPackDto {

    /**
     * 红包总金额
     */
    @NotNull
    private Integer redMoney;

    /**
     * 发红包的人的账号
     */
    @NotBlank
    private String userAccount;

    /**
     * 发红包的人的昵称
     */
    @NotBlank
    private String userName;

    /**
     * 红包类型 luck 拼手气  normal 普通
     */
    @NotBlank
    private String redType;

    /**
     * 红包数量
     */
    @NotNull
    private Integer redNum;

    /**
     * 红包祝福语
     */
    private String redContent;

    /**
     * 红包面向对象(群聊group 或单聊personal)
     */
    private String redObject;

    /**
     * 对象id(群组id或个人账号)
     */
    private String objectAccount;

    /**
     * 对象名称(群组名或个人名)
     */
    private String objectName;

    /**
     * 盐
     */
    private String salt;

}
