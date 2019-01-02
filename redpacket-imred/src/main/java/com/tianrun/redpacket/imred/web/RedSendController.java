package com.tianrun.redpacket.imred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.imred.dto.InRedPackDto;
import com.tianrun.redpacket.imred.dto.InRedPayFallbackDto;
import com.tianrun.redpacket.imred.dto.OutRedPackDto;
import com.tianrun.redpacket.imred.service.RedSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@RestController
public class RedSendController {

    @Autowired
    private RedSendService redSendService;


    /**
     * 包红包
     * @param inRedPackDto
     * @return
     * @throws Exception
     */
    @PostMapping("/pack")
    public RP redPack(@RequestBody @Valid InRedPackDto inRedPackDto) throws Exception{

        OutRedPackDto outRedPackDto = redSendService.redPack(inRedPackDto);

        return RP.buildSuccess(outRedPackDto);
    }


    /**
     * 支付结果通知
     * @throws Exception
     */
    @PostMapping("/payfallback")
    public RP payFallback(@RequestBody @Valid InRedPayFallbackDto inRedPayFallbackDto) throws Exception{

        // 这里返回红包id是为了测试
        Long redId = redSendService.payFallback(inRedPayFallbackDto);
        return RP.buildSuccess("succcess",redId);
        // TODO 修改支付状态，发放红包，设置redis红包信息
    }

}
