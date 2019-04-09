package com.tianrun.redpacket.countred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.common.dict.RedDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Mapper
public interface IndexMapper{

    /**
     * 获取im红包发送数量
     * @param date
     * @return
     */
    @Select("SELECT\n" +
            "red_object,\n" +
            "COUNT(1) as num\n" +
            "FROM\n" +
            "tb_red_im_distribute\n" +
            "WHERE\n" +
            "TO_DAYS(send_time) = TO_DAYS(#{date})\n" +
            "GROUP BY\n" +
            "red_object\n")
    List<Map<String,Double>> getImSendByDay(@Param("date") Date date);


    /**
     * 获取im红包发送金额
     * @param date
     * @return
     */
    @Select("SELECT\n" +
            "red_object,\n" +
            "sum(red_money) as total\n" +
            "FROM\n" +
            "tb_red_im_distribute\n" +
            "WHERE\n" +
            "TO_DAYS(send_time) = TO_DAYS(#{date})\n" +
            "GROUP BY\n" +
            "red_object")
    List<Map<String,Double>> getImMoneyByDay(@Param("date") Date date);


    /**
     * 获取im红包趋势
     * @param begin
     * @param end
     * @return
     */
    @Select("SELECT\n" +
            "DATE_FORMAT(send_time,'%Y-%m-%d') as dateFormat,\n" +
            "count(1) as sum,\n" +
            "SUM(red_money) as total\n" +
            "FROM\n" +
            "tb_red_im_distribute\n" +
            "WHERE\n" +
            "TO_DAYS(send_time) >= TO_DAYS(#{begin}) AND TO_DAYS(send_time) < TO_DAYS(#{end})\n" +
            "GROUP BY\n" +
            "DATE_FORMAT(send_time,'%Y-%m-%d')\n" +
            "ORDER BY DATE_FORMAT(send_time,'%Y-%m-%d') ASC\n")
    List<Map<String,String>> getImTrend(@Param("begin") Date begin,@Param("end")Date end);
}
