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

    /**
     * 抢红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    @GetMapping("/grab")
    public RP<OutGrabDto> grab(@RequestParam("redNo")String redNo,@RequestParam("userAccount")String userAccount) throws Exception{
        return RP.buildSuccess(redGrabService.grab(redNo,userAccount));
    }

    @GetMapping("/grab/test")
    public RP testGrab(@RequestParam("redId")String redNo) throws Exception{
        String userAccount = RandomStringUtils.randomAlphanumeric(5);
        return RP.buildSuccess(redGrabService.grab(redNo,userAccount));
    }

    @GetMapping("/unpack/test")
    public RP testUnpack(@RequestParam("redNo")String redNo) throws Exception{
        String userAccount = RandomStringUtils.randomAlphanumeric(5);
        return RP.buildSuccess(redGrabService.unpack(redNo,userAccount));
    }

    /**
     * 拆红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    @GetMapping("/unpack")
    public RP<OutUnpackDto> unpack(@RequestParam("redNo")String redNo, @RequestParam("userAccount")String userAccount) throws Exception{
        return RP.buildSuccess(redGrabService.unpack(redNo,userAccount));
    }


}
