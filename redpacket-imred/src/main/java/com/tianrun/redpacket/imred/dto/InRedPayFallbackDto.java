package com.tianrun.redpacket.imred.dto;

import lombok.Data;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
@Data
public class InRedPayFallbackDto {

    private String orderNo;

    private boolean payFlag;

    private String payWay;

}
