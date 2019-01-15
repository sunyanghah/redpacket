package com.tianrun.redpacket.imred.service;

import com.tianrun.redpacket.imred.dto.OutGrabDto;
import com.tianrun.redpacket.imred.dto.OutRedUnpackInfoDto;
import com.tianrun.redpacket.imred.dto.OutUnpackDto;
import com.tianrun.redpacket.imred.entity.RedGrab;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
public interface RedGrabService {

    /**
     * 抢红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
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

    /**
     * 获取红包被抢信息
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    OutRedUnpackInfoDto getUnpackInfo(String redNo, String userAccount) throws Exception;
}
