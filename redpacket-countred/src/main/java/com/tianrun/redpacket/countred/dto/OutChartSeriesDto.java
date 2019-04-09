package com.tianrun.redpacket.countred.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Data
public class OutChartSeriesDto {

    private String name;

    private List<Double> data;

}
