package com.tianrun.redpacket.countred.dto;

import lombok.Data;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Data
public class OutChartDataDto {

    private String name;


    private Double value;

    public OutChartDataDto(){}

    public OutChartDataDto(String name,Double value) {
        this.name = name;
        this.value = value;
    }
}
