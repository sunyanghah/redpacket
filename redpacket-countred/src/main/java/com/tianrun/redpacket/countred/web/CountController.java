package com.tianrun.redpacket.countred.web;

import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.countred.dto.OutChartBarDto;
import com.tianrun.redpacket.countred.service.CountService;
import com.tianrun.redpacket.countred.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@RestController
@RequestMapping("/count")
public class CountController {

    @Autowired
    private CountService countService;
    @Autowired
    private IndexService indexService;

    @PostMapping("/imNum")
    public RP getImNumCount() throws Exception{

        return RP.buildSuccess(null);
    }

    @PostMapping("/imTotal")
    public RP getImTotalCount() throws Exception{

        return RP.buildSuccess(null);
    }

    /**
     * 近30天趋势
     * @return
     * @throws Exception
     */
    @PostMapping("/imTrend")
    public RP getImTrendCount() throws Exception{
        Calendar nowCalendar = Calendar.getInstance();
//        nowCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-01-26 16:09:22"));
        Date nowDate = nowCalendar.getTime();
        nowCalendar.add(Calendar.DAY_OF_YEAR,-30);
        OutChartBarDto outChartBarDto = indexService.getImTrendCount(nowCalendar.getTime(),nowDate);
        return RP.buildSuccess(outChartBarDto);
    }
}
