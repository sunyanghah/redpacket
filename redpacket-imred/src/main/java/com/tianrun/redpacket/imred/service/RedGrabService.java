package com.tianrun.redpacket.imred.service;

import com.tianrun.redpacket.imred.dto.OutGrabDto;
import com.tianrun.redpacket.imred.dto.OutUnpackDto;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
public interface RedGrabService {

    /**
     * 抢红包
     * @param redId
     * @param userAccount
     * @return
     * @throws Exception
     */
    OutGrabDto grab(Long redId, String userAccount) throws Exception;

    /**
     * 拆红包
     * @param redId
     * @param userAccount
     * @return
     * @throws Exception
     */
    OutUnpackDto unpack(Long redId, String userAccount) throws Exception;
}
