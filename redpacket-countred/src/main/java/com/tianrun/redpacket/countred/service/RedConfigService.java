package com.tianrun.redpacket.countred.service;

import com.tianrun.redpacket.countred.dto.InUpdateConfigDto;
import com.tianrun.redpacket.common.dto.OutRedConfigDto;

import java.util.List;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
public interface RedConfigService {

    /**
     * 修改红包配置
     * @param inUpdateConfigDto
     * @throws Exception
     */
    void updateConfig(InUpdateConfigDto inUpdateConfigDto) throws Exception;

    /**
     * 获取红包配置列表
     * @return
     * @throws Exception
     */
    List<OutRedConfigDto> getConfigList() throws Exception;

    /**
     * 根据红包配置code获取配置详情
     * @param configCode
     * @return
     * @throws Exception
     */
    OutRedConfigDto getConfigByCode(String configCode) throws Exception;
}
