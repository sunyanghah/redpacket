package com.tianrun.redpacket.companyred.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.entity.RedTaskResult;

import java.util.List;

/**
 * Created by dell on 2019/1/18.
 * @author dell
 */
public interface TaskResultService extends IService<RedTaskResult>{

    /**
     * 获取全部完成某些任务的人员列表
     * @param taskInfos
     * @param taskNum
     * @return
     */
    List<String> getAccountByFinishAllTask(List<RedTask> taskInfos, Integer taskNum);
}
