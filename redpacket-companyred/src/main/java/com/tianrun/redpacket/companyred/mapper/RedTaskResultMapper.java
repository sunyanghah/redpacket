package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.entity.RedTaskResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dell on 2019/1/18.
 * @author dell
 */
@Mapper
public interface RedTaskResultMapper extends BaseMapper<RedTaskResult>{


    /**
     * 获取全部完成某些任务的人员列表
     * @param taskInfos
     * @param taskNum
     * @return
     */
    @Select("<script>" +
            "SELECT\n" +
            "user_account\n" +
            "FROM\n" +
            "tb_red_task_result\n" +
            "WHERE\n" +
            "<foreach collection=\"taskInfos\" item=\"taskInfo\" separator=\"OR\">\n" +
            "(task_id = #{taskInfo.id} and task_status = #{taskInfo.finishedCode})\n" +
            "</foreach>\n" +
            "GROUP BY\n" +
            "user_account\n" +
            "HAVING\n" +
            "count(*) = #{taskNum}\n " +
            "</script>")
    List<String> getAccountByFinishAllTask(@Param("taskInfos")List<RedTask> taskInfos,
                                           @Param("taskNum")Integer taskNum);
}
