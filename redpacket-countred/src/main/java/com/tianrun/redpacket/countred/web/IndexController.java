package com.tianrun.redpacket.countred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.countred.dto.OutIndexDto;
import com.tianrun.redpacket.countred.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/index")
    public RP<OutIndexDto> index() throws Exception{

        OutIndexDto outIndexDto = new OutIndexDto();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-01-26 16:09:22"));


        Date nowDate = nowCalendar.getTime();
        outIndexDto.setImSend(indexService.getImSendByDayCount(nowDate));
        outIndexDto.setImMoney(indexService.getImMoneyByDayCount(nowDate));

        nowCalendar.add(Calendar.DAY_OF_YEAR,-7);
        outIndexDto.setImTrend(indexService.getImTrendCount(nowCalendar.getTime(),nowDate));

        return RP.buildSuccess(outIndexDto);
    }

}
