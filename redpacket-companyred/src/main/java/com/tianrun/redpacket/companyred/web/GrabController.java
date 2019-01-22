package com.tianrun.redpacket.companyred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.companyred.service.GrabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dell on 2019/1/18.
 * @author dell
 */
@RestController
public class GrabController {

    @Autowired
    private GrabService grabService;

    /**
     * 企业红包抢红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    @GetMapping("/grab")
    public RP grab(@RequestParam("redNo")String redNo, @RequestParam("userAccount")String userAccount) throws Exception{
        return RP.buildSuccess(grabService.grab(redNo,userAccount));
    }

    /**
     * 企业红包拆红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    @GetMapping("/unpack")
    public RP unpack(@RequestParam("redNo")String redNo, @RequestParam("userAccount")String userAccount) throws Exception{

        return RP.buildSuccess(grabService.unpack(redNo,userAccount));
    }
}
