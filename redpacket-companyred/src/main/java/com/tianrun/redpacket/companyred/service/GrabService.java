package com.tianrun.redpacket.companyred.service;

import com.tianrun.redpacket.companyred.dto.OutGrabDto;
import com.tianrun.redpacket.companyred.dto.OutUnpackDto;

/**
 * Created by dell on 2019/1/21.
 * @author dell
 */
public interface GrabService{
    /**
     * 抢红包
     * @param redNo
     * @param userAccount
     * @return
     */
    OutGrabDto grab(String redNo, String userAccount) throws Exception;

    /**
     * 拆红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    OutUnpackDto unpack(String redNo, String userAccount) throws Exception;
}
