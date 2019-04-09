package com.tianrun.redpacket.countred.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Data
public class OutChartBarDto {

    private List<String> legend;

    private List<String> xData;

    private List<OutChartSeriesDto> series;

    public List<OutChartSeriesDto> getSeries(){

        if (series == null){
            series = new ArrayList<>();
        }

        if (legend != null && legend.size() > 0){

            if (legend.size() > series.size()) {
                OutChartSeriesDto outChartSeriesDto;
                for (String item : legend) {
                    outChartSeriesDto = new OutChartSeriesDto();
                    outChartSeriesDto.setName(item);
                    series.add(outChartSeriesDto);
                }
            }

            if (xData != null && xData.size() > 0){
                series.stream().filter(outChartSeriesDto -> null == outChartSeriesDto.getData())
                        .forEach(outChartSeriesDto -> outChartSeriesDto.setData(new ArrayList<>()));
            }
        }

        return series;
    }
}
