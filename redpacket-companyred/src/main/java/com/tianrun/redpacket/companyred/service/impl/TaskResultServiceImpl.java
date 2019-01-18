package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.entity.RedTaskResult;
import com.tianrun.redpacket.companyred.mapper.RedTaskResultMapper;
import com.tianrun.redpacket.companyred.service.TaskResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dell on 2019/1/18.
 * @author dell
 */
@Service
public class TaskResultServiceImpl extends ServiceImpl<RedTaskResultMapper,RedTaskResult> implements TaskResultService {

    @Autowired
    private RedTaskResultMapper redTaskResultMapper;

    @Override
    public List<String> getAccountByFinishAllTask(List<RedTask> taskInfos, Integer taskNum) {
        return redTaskResultMapper.getAccountByFinishAllTask(taskInfos,taskNum);
    }
}
