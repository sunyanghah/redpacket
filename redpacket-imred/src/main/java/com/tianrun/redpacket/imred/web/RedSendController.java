package com.tianrun.redpacket.imred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.imred.dto.InRedPackDto;
import com.tianrun.redpacket.imred.dto.InRedPayFallbackDto;
import com.tianrun.redpacket.imred.dto.OutRedPackDto;
import com.tianrun.redpacket.imred.service.RedSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private RedisTemplate redisTemplate;
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
        redSendService.payFallback(inRedPayFallbackDto);
        return RP.buildSuccess("success");
        // TODO 修改支付状态，发放红包，设置redis红包信息
    }

    @GetMapping("/redisTest")
    public RP redisTest() throws Exception{
        redisTemplate.opsForValue().set("sytest","dsfsdfsdfsdf");
        Object obj = redisTemplate.opsForValue().get("sytest");
        return RP.buildSuccess(obj);
    }
}
