package com.tianrun.redpacket.imred.service;

import com.tianrun.redpacket.imred.dto.InRedPackDto;
import com.tianrun.redpacket.imred.dto.InRedPayFallbackDto;
import com.tianrun.redpacket.imred.dto.OutRedPackDto;

import javax.validation.Valid;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
public interface RedSendService {

    /**
     * 包红包
     * @param inRedPackDto
     * @return
     * @throws Exception
     */
    OutRedPackDto redPack(InRedPackDto inRedPackDto) throws Exception;

    /**
     * 支付结果通知
     * @param inRedPayFallbackDto
     * @throws Exception
     */
    void payFallback(InRedPayFallbackDto inRedPayFallbackDto) throws Exception;
}
