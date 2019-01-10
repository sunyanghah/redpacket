package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.companyred.entity.RedTaskRel;
import com.tianrun.redpacket.companyred.mapper.RedTaskRelMapper;
import com.tianrun.redpacket.companyred.service.TaskRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Service
public class TaskRelServiceImpl extends ServiceImpl<RedTaskRelMapper,RedTaskRel> implements TaskRelService {

    @Autowired
    private RedTaskRelMapper redTaskRelMapper;

    @Override
    public List<Long> getTaskListByRedId(Long redId) throws Exception {
        return redTaskRelMapper.getTaskListByRedId(redId);
    }

    @Override
    public void deleteTaskRelOfRed(Long redId) throws Exception {
        redTaskRelMapper.deleteTaskRelOfRed(redId);
    }
}
