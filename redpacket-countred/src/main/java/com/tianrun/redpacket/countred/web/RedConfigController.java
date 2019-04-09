package com.tianrun.redpacket.countred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.countred.dto.InUpdateConfigDto;
import com.tianrun.redpacket.common.dto.OutRedConfigDto;
import com.tianrun.redpacket.countred.service.RedConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@RestController
@RequestMapping("/config")
public class RedConfigController {

    @Autowired
    private RedConfigService redConfigService;


    /**
     * 修改配置值
     * @param inUpdateConfigDto
     * @return
     * @throws Exception
     */
    @PutMapping()
    public RP updateConfig(@RequestBody @Valid InUpdateConfigDto inUpdateConfigDto) throws Exception{
        redConfigService.updateConfig(inUpdateConfigDto);
        return RP.buildSuccess("配置修改成功");
    }

    /**
     * 获取配置列表
     * @return
     * @throws Exception
     */
    @GetMapping
    public RP getConfigList() throws Exception{
        List<OutRedConfigDto> redConfigDtoList = redConfigService.getConfigList();
        return RP.buildSuccess(redConfigDtoList);
    }

    /**
     * 根据配置code获取配置信息
     * @param configCode
     * @return
     * @throws Exception
     */
    @GetMapping("/{configCode}")
    public RP getConfigByCode(@PathVariable("configCode")String configCode) throws Exception{
        OutRedConfigDto outRedConfigDto = redConfigService.getConfigByCode(configCode);
        return RP.buildSuccess(outRedConfigDto);
    }
}
