package com.tianrun.redpacket.countred.service;

import com.tianrun.redpacket.countred.dto.OutChartBarDto;
import com.tianrun.redpacket.countred.dto.OutChartPieDto;
import com.tianrun.redpacket.countred.dto.OutChartSeriesDto;

import java.util.Date;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
public interface IndexService {

    /**
     * 当日IM红包发放数量概览
     * @param date
     * @return
     * @throws Exception
     */
    OutChartPieDto getImSendByDayCount(Date date) throws Exception;

    /**
     * 当日IM红包发放金额概览
     * @param date
     * @return
     * @throws Exception
     */
    OutChartPieDto getImMoneyByDayCount(Date date) throws Exception;

    /**
     * 红包收发量趋势
     * @param begin
     * @param end
     * @return
     * @throws Exception
     */
    OutChartBarDto getImTrendCount(Date begin, Date end) throws Exception;

}
