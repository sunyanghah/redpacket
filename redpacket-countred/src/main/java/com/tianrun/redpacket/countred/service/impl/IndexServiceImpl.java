package com.tianrun.redpacket.countred.service.impl;

import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.countred.dto.OutChartBarDto;
import com.tianrun.redpacket.countred.dto.OutChartDataDto;
import com.tianrun.redpacket.countred.dto.OutChartPieDto;
import com.tianrun.redpacket.countred.dto.OutChartSeriesDto;
import com.tianrun.redpacket.countred.mapper.IndexMapper;
import com.tianrun.redpacket.countred.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private IndexMapper indexMapper;

    private String personalNum = "个人红包发放数量";
    private String groupNum = "群组红包发放数量";
    private String personalMoney = "个人红包发放金额";
    private String groupMoney = "群组红包发放金额";
    private String numTrend = "IM红包发放数量趋势";
    private String moneyTrend = "IM红包发放金额趋势";


    @Override
    public OutChartPieDto getImSendByDayCount(Date date) throws Exception{
        OutChartPieDto outChartPieDto = new OutChartPieDto();
        outChartPieDto.setLegend(Arrays.asList(personalNum,groupNum));
        List<Map<String,Double>> countData = indexMapper.getImSendByDay(date);
        List<OutChartDataDto> outChartDataDtos = new ArrayList<>();
        if (countData != null && countData.size() > 0){
            for (Map<String,Double> map : countData){
                if (DictConstant.RED_OBJECT_PERSONAL.equals(map.get(DictConstant.RED_OBJECT))){
                    outChartDataDtos.add(new OutChartDataDto(personalNum,new Double(String.valueOf(map.get("num")))));
                }else if (DictConstant.RED_OBJECT_GROUP.equals(map.get(DictConstant.RED_OBJECT))){
                    outChartDataDtos.add(new OutChartDataDto(groupNum,new Double(String.valueOf(map.get("num")))));
                }
            }
        }
        outChartPieDto.setData(outChartDataDtos);
        return outChartPieDto;
    }


    @Override
    public OutChartPieDto getImMoneyByDayCount(Date date) throws Exception{
        OutChartPieDto outChartPieDto = new OutChartPieDto();
        outChartPieDto.setLegend(Arrays.asList(personalMoney,groupMoney));
        List<Map<String,Double>> countData = indexMapper.getImMoneyByDay(date);
        List<OutChartDataDto> outChartDataDtos = new ArrayList<>();
        if (countData != null && countData.size() > 0){
            for (Map<String,Double> map : countData){
                if (DictConstant.RED_OBJECT_PERSONAL.equals(map.get(DictConstant.RED_OBJECT))){
                    outChartDataDtos.add(new OutChartDataDto(personalMoney,new Double(String.valueOf(map.get("total")))));
                }else if (DictConstant.RED_OBJECT_GROUP.equals(map.get(DictConstant.RED_OBJECT))){
                    outChartDataDtos.add(new OutChartDataDto(groupMoney,new Double(String.valueOf(map.get("total")))));
                }
            }
        }
        outChartPieDto.setData(outChartDataDtos);
        return outChartPieDto;
    }

    @Override
    public OutChartBarDto getImTrendCount(Date begin, Date end) throws Exception {
        OutChartBarDto outChartBarDto = new OutChartBarDto();
        outChartBarDto.setLegend(Arrays.asList(numTrend,moneyTrend));
        List<String> xData = getXDataByDay(begin,end);
        outChartBarDto.setXData(xData);

        List<Map<String,String>> imTrendData = indexMapper.getImTrend(begin,end);
        if (imTrendData != null && imTrendData.size() > 0){
            List<OutChartSeriesDto> seriesDtoList = new ArrayList<>();
            List<Double> numData = new ArrayList<>();
            List<Double> moneyData = new ArrayList<>();
            for (String x : xData){
                boolean hasDataFlag = false;
                for (Map<String,String> map : imTrendData){
                    if (x.equals(map.get("dateFormat"))){
                        hasDataFlag = true;
                        numData.add(new Double(String.valueOf(map.get("sum"))));
                        moneyData.add(new Double(String.valueOf(map.get("total"))));
                    }
                }
                if (!hasDataFlag){
                    numData.add(0D);
                    moneyData.add(0D);
                }
            }

            for (String legend : outChartBarDto.getLegend()){
                OutChartSeriesDto outChartSeriesDto = new OutChartSeriesDto();
                outChartSeriesDto.setName(legend);
                if (legend.equals(numTrend)){
                    outChartSeriesDto.setData(numData);
                }else if (legend.equals(moneyTrend)){
                    outChartSeriesDto.setData(moneyData);
                }
                seriesDtoList.add(outChartSeriesDto);
            }
            outChartBarDto.setSeries(seriesDtoList);
        }

        return outChartBarDto;
    }

    private List<String> getXDataByDay(Date begin, Date end) throws Exception {
        List<String> xData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(begin);
        while (end.after(beginCal.getTime())){
            xData.add(sdf.format(beginCal.getTime()));
            beginCal.add(Calendar.DAY_OF_YEAR,1);
        }
        return xData;
    }

}
