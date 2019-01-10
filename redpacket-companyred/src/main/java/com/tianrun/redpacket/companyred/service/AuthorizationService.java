package com.tianrun.redpacket.companyred.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.companyred.entity.RedAuthorization;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
public interface AuthorizationService extends IService<RedAuthorization> {

    /**
     * 根据红包id获取领取人员范围信息
     * @param id
     * @return
     * @throws Exception
     */
    List<RedAuthorization> getAuthListByRedId(Long id) throws Exception;

    /**
     * 根据红包id删除领取人员范围信息
     * @param redActivityId
     * @throws Exception
     */
    void deleteAuthOfRed(long redActivityId) throws Exception;
}
