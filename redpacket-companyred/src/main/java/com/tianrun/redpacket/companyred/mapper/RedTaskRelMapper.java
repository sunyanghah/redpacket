package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.companyred.entity.RedTaskRel;
import org.apache.ibatis.annotations.Mapper;

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
}
