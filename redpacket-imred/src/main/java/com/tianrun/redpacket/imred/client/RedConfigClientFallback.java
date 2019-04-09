package com.tianrun.redpacket.imred.client;

import com.tianrun.redpacket.common.dto.OutRedConfigDto;
import com.tianrun.redpacket.common.platform.RP;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by dell on 2019/4/9.
 * @author dell
 */
@Component
public class RedConfigClientFallback implements RedConfigClient {
    @Override
    public RP<OutRedConfigDto> getConfigByCode(@PathVariable("configCode") String configCode) throws Exception {
        return null;
    }
}
