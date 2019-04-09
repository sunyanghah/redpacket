package com.tianrun.redpacket.countred.dto;

import lombok.Data;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Data
public class OutIndexDto {

    private OutChartPieDto imSend;

    private OutChartPieDto imMoney;

    private OutChartBarDto imTrend;
}
