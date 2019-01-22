package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.common.dict.DictHandle;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.companyred.dto.*;
import com.tianrun.redpacket.companyred.entity.RedActivity;
import com.tianrun.redpacket.companyred.entity.RedTask;
import com.tianrun.redpacket.companyred.entity.RedTaskResult;
import com.tianrun.redpacket.companyred.mapper.RedTaskMapper;
import com.tianrun.redpacket.companyred.mapper.RedTaskRelMapper;
import com.tianrun.redpacket.companyred.mapper.RedTaskResultMapper;
import com.tianrun.redpacket.companyred.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedTaskResultMapper redTaskResultMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedTaskRelMapper redTaskRelMapper;

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

    @Override
    public void changeStatus(InChangeTaskStatusDto inChangeTaskStatusDto) throws Exception {
        RedTask redTask = new RedTask();
        redTask.setTaskCode(inChangeTaskStatusDto.getTaskCode());
        redTask = redTaskMapper.selectOne(new QueryWrapper<>(redTask));
        if (redTask == null){
            throw new BusinessException("没有此任务");
        }
        if (!inChangeTaskStatusDto.getSecureKey().equals(redTask.getSecureKey())){
            throw new BusinessException("错误的secureKey");
        }
        RedTaskResult result = redTaskResultMapper.getStatusByTaskAndUser(inChangeTaskStatusDto.getTaskCode(),
                inChangeTaskStatusDto.getUserAccount());
        if (result == null){
            result = new RedTaskResult();
            result.setId(idGenerator.next());
            result.setTaskId(redTask.getId());
            result.setUserAccount(inChangeTaskStatusDto.getUserAccount());
            result.setTaskStatus(inChangeTaskStatusDto.getTaskStatus());
            redTaskResultMapper.insert(result);
        }else {
            result.setTaskStatus(inChangeTaskStatusDto.getTaskStatus());
            redTaskResultMapper.updateById(result);
        }
        // 如果完成了任务
        if (redTask.getFinishedCode().equals(inChangeTaskStatusDto.getTaskStatus())){
            //获取任务关联的所有活动
            List<RedActivity> redActivityList = redTaskRelMapper.getActivityByTask(redTask.getId());
            if (redActivityList != null && redActivityList.size() > 0){
                // 只要不是未激活
                redActivityList.stream().filter(redActivity ->
                        !DictConstant.ACTIVITY_STATUS_UNACTIVE.equals(redActivity.getActivityStatus()))
                        .forEach(redActivity ->
                    redisTemplate.opsForList().rightPush(RedConstants.HB_TASK + redActivity.getRedNo(),
                            inChangeTaskStatusDto.getUserAccount())
                );
            }
        }
    }
}
