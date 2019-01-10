package com.tianrun.redpacket.imred.dto;

import lombok.Data;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
@Data
public class InRedPayFallbackDto {

    /**
     * 原商户请求号
     */
    private String requestNo;

    /**
     * 商户用户标识
     */
    private String merchantUserId;

    /**
     * 订单金额
     */
    private Double orderAmount;

    /**
     * 需支付金额
     */
    private Double fundAmount;

    /**
     * 已付金额
     */
    private Double paidAmount;

    /**
     * 订单状态
     * SUCCESS-支付成功
     */
    private String status;

    /**
     * 支付方式
     * BALANCE-余额支付
     * BINDCARD-绑卡支付
     * WECHATSCAN-微信用户扫码支付
     * YEEPAYCASHIER-易宝银行卡支付
     * SALESB2C-网银 B2C
     * SALESB2B-网银 B2B
     * WECHATOFFICIAL-微信公众号支付
     * ALIPAYSCAN-支付宝用户扫码支付
     * ALIPAYAPP-支付宝 APP 支付
     */
    private String payTool;

    /**
     * 卡号后四位
     */
    private String cardLast;

    /**
     * 银行卡类别
     */
    private String cardType;

    /**
     * 银行编码
     */
    private String bankCode;

    /**
     * 营销信息
     */
    private String couponInfo;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 微信openID
     */
    private String openID;

    /**
     * 微信unionID
     */
    private String unionID;

    /**
     * 银行订单号
     */
    private String bankOrderId;

}
