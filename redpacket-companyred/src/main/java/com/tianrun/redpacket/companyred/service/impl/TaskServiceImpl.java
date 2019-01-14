package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.common.dict.DictHandle;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.companyred.dto.*;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.mapper.RedTaskMapper;
import com.tianrun.redpacket.companyred.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Service
public class TaskServiceImpl extends ServiceImpl<RedTaskMapper,RedTask> implements TaskService{

    @Autowired
    private RedTaskMapper redTaskMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private DictHandle dictHandle;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTask(InAddTaskDto inAddTaskDto) throws Exception {
        RedTask redTask = new RedTask();
        BeanUtils.copyProperties(inAddTaskDto,redTask);
        redTask.setId(idGenerator.next());
        redTask.preInsert("123");
        redTaskMapper.insert(redTask);
    }

    @Override
    public OutGetTaskDto getTask(Long id) throws Exception {
        OutGetTaskDto outGetTaskDto = null;
        RedTask redTask = new RedTask();
        redTask.setId(id);
        redTask.setDelFlag("0");
        redTask = redTaskMapper.selectOne(new QueryWrapper<>(redTask));
        if (redTask != null) {
            outGetTaskDto = new OutGetTaskDto();
            BeanUtils.copyProperties(redTask, outGetTaskDto);
            dictHandle.handleDict(outGetTaskDto, "accessFlag", "yes_no");
            dictHandle.handleDict(outGetTaskDto, "usefulFlag", "yes_no");
        }
        return outGetTaskDto;
    }

    @Override
    public Page queryTask(InQueryTaskDto inQueryTaskDto) throws Exception {
        Page page = new Page();
        page.setCurrent(inQueryTaskDto.getCurrent());
        page.setSize(inQueryTaskDto.getSize());
        List<OutQueryTaskDto> list = redTaskMapper.queryTask(page,inQueryTaskDto);
        dictHandle.handleDict(list, "accessFlag", "yes_no");
        dictHandle.handleDict(list, "usefulFlag", "yes_no");
        page.setRecords(list);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(InUpdateTaskDto inUpdateTaskDto) throws Exception {
        RedTask redTask = new RedTask();
        BeanUtils.copyProperties(inUpdateTaskDto,redTask);
        redTask.preUpdate("123");
        redTaskMapper.updateById(redTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(InBatchIdDto<Long> inBatchIdDto) throws Exception {
        redTaskMapper.deleteTask(inBatchIdDto);
    }
}
