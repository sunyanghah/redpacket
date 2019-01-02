package com.tianrun.redpacket.imred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.imred.dto.OutGrabDto;
import com.tianrun.redpacket.imred.dto.OutUnpackDto;
import com.tianrun.redpacket.imred.service.RedGrabService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
@RestController
public class RedGrabController {

    @Autowired
    private RedGrabService redGrabService;

    @GetMapping("/grab")
    public RP<OutGrabDto> grab(@RequestParam("redId")Long redId,@RequestParam("userAccount")String userAccount) throws Exception{
        return RP.buildSuccess(redGrabService.grab(redId,userAccount));
    }

    @GetMapping("/grab/test")
    public RP testGrab(@RequestParam("redId")Long redId) throws Exception{
        String userAccount = RandomStringUtils.randomAlphanumeric(5);
        return RP.buildSuccess(redGrabService.grab(redId,userAccount));
    }

    @GetMapping("/just/test")
    public RP justTest() throws Exception{
        return RP.buildSuccess("");
    }

    @GetMapping("/unpack/test")
    public RP testUnpack(@RequestParam("redId")Long redId) throws Exception{
        String userAccount = RandomStringUtils.randomAlphanumeric(5);
        return RP.buildSuccess(redGrabService.unpack(redId,userAccount));
    }

    @GetMapping("/unpack")
    public RP<OutUnpackDto> unpack(@RequestParam("redId")Long redId, @RequestParam("userAccount")String userAccount) throws Exception{
        return RP.buildSuccess(redGrabService.unpack(redId,userAccount));
    }


}
