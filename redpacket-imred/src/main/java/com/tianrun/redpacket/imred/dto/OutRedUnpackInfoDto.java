package com.tianrun.redpacket.imred.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by dell on 2018/12/24.
 * @author dell
 */
@Data
public class OutRedUnpackInfoDto {

    /**
     * 红包id
     */
    private Long redId;

    /**
     * 红包类型 luck 拼手气  normal 普通
     */
    private String redType;

    /**
     * 红包祝福语
     */
    private String redContent;

    /**
     * 发送人账号
     */
    private String senderAccount;

    /**
     * 发送人姓名
     */
    private String senderName;

    /**
     * 已拆数量
     */
    private Integer unpackNum;

    /**
     * 红包总数
     */
    private Integer redNum;

    /**
     * 我抢到的钱数，没抢到时返回null
     */
    private Integer myUnpack;

    /**
     * 拆红包的列表
     */
    private List<RedUnpackInfoDto> unpackInfoList;

}
