package com.tianrun.redpacket.countred.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Data
public class OutChartPieDto {

    private List<String> legend;

    private List<OutChartDataDto> data;


    public List<OutChartDataDto> getData(){
        if (data == null){
            data = new ArrayList<>();
        }
        if (legend != null && legend.size() > 0 && legend.size() > data.size()){
            for (String item : legend){
                boolean hasNot = true;
                for (OutChartDataDto outChartDataDto : data){
                    if (item.equals(outChartDataDto.getName())){
                        hasNot = false;
                    }
                }
                if (hasNot){
                    data.add(new OutChartDataDto(item,0D));
                }
            }
        }
        return data;
    }
}
