package com.tianrun.redpacket.companyred.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.entity.RedTaskRel;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
public interface TaskRelService extends IService<RedTaskRel> {

    /**
     * 根据红包id查询任务
     * @param redId
     * @return
     * @throws Exception
     */
    List<Long> getTaskListByRedId(Long redId) throws Exception;

    /**
     * 根据红包id删除红包的前置任务
     * @param redId
     * @throws Exception
     */
    void deleteTaskRelOfRed(Long redId) throws Exception;

    /**
     * 根据红包id查询任务信息
     * @param redId
     * @return
     * @throws Exception
     */
    List<RedTask> getTaskInfoByRedId(Long redId) throws Exception;
}
