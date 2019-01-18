package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.entity.RedTaskRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Mapper
public interface RedTaskRelMapper extends BaseMapper<RedTaskRel> {

    /**
     * 根据红包id查询任务
     * @param redId
     * @return
     */
    List<Long> getTaskListByRedId(Long redId);

    /**
     * 根据红包id删除红包的前置任务
     * @param redId
     */
    void deleteTaskRelOfRed(Long redId);

    /**
     * 根据红包id获取任务信息
     * @param redId
     * @return
     */
    @Select("SELECT\n" +
            "rt.*\n" +
            "FROM\n" +
            "tb_red_activity ra\n" +
            "INNER JOIN\n" +
            "tb_red_task_rel rtr\n" +
            "ON\n" +
            "ra.id = rtr.red_id\n" +
            "INNER JOIN\n" +
            "tb_red_task rt\n" +
            "ON\n" +
            "rt.id = rtr.task_id\n" +
            "WHERE\n" +
            "ra.del_flag = '0'\n" +
            "AND\n" +
            "rt.del_flag = '0'\n" +
            "AND\n" +
            "red_id = #{redId}\n")
    List<RedTask> getTaskInfoByRedId(@Param("redId") Long redId);
}
