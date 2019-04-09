package com.tianrun.redpacket.imred.client;

import com.tianrun.redpacket.common.dto.OutRedConfigDto;
import com.tianrun.redpacket.common.platform.RP;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by dell on 2019/4/9.
 * @author dell
 */
@FeignClient(value = "redpacket-countred",fallback = RedConfigClientFallback.class)
public interface RedConfigClient {

    /**
     * 根据配置code获取配置值
     * @param configCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/red/count/config/{configCode}",method = RequestMethod.GET)
    RP<OutRedConfigDto> getConfigByCode(@PathVariable("configCode")String configCode) throws Exception;

}
