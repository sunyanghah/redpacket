package com.tianrun.redpacket.imred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.imred.entity.RedGrab;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Mapper
public interface RedGrabMapper extends BaseMapper<RedGrab> {

    /**
     * 获取拼手气红包中，运气最佳的记录
     * @param redNo
     * @return
     */
    @Select("SELECT\n" +
            "*\n" +
            "FROM\n" +
            "tb_red_grab\n" +
            "WHERE\n" +
            "red_no = #{redNo}\n" +
            "ORDER BY\n" +
            "money DESC,grab_time ASC\n" +
            "LIMIT 0,1")
    RedGrab getBestLuckRedGrab(@Param("redNo") String redNo);

    /**
     * 获取红包已抢的钱数
     * @param redNo
     * @return
     */
    @Select("SELECT\n" +
            "SUM(money)\n" +
            "FROM\n" +
            "tb_red_grab\n" +
            "WHERE\n" +
            "red_no = #{redNo}\n")
    Integer getGrabMoneyByRedNo(@Param("redNo") String redNo);

}
