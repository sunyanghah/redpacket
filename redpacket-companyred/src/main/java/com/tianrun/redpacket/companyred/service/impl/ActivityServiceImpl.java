package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.DictHandle;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.companyred.dto.*;
import com.tianrun.redpacket.companyred.entity.RedActivity;
import com.tianrun.redpacket.companyred.entity.RedAuthorization;
import com.tianrun.redpacket.companyred.entity.RedTaskRel;
import com.tianrun.redpacket.companyred.mapper.RedActivityMapper;
import com.tianrun.redpacket.companyred.service.ActivityService;
import com.tianrun.redpacket.companyred.service.AuthorizationService;
import com.tianrun.redpacket.companyred.service.TaskRelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<RedActivityMapper,RedActivity> implements ActivityService {

    @Autowired
    private RedActivityMapper redActivityMapper;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private TaskRelService taskRelService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private DictHandle dictHandle;

    /**
     * 新增企业红包活动
     * @param inAddActivityDto
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addActivity(InAddActivityDto inAddActivityDto) throws Exception {

        // 红包活动
        RedActivity redActivity = new RedActivity();
        BeanUtils.copyProperties(inAddActivityDto,redActivity);
        long redActivityId = idGenerator.next();
        redActivity.setId(redActivityId);
        redActivity.preInsert("123");
        redActivity.setActivityStatus(DictConstant.ACTIVITY_STATUS_UNACTIVE);
        if (DictConstant.RED_TYPE_NORMAL.equals(redActivity.getRedType())){
            redActivity.setRedAmount(redActivity.getRedNum()*redActivity.getRedPrice());
        }
        redActivityMapper.insert(redActivity);

        // 领取人员范围
        addAuth(inAddActivityDto,redActivityId);

        // 前置任务
        addTaskRel(inAddActivityDto,redActivityId);

    }

    @Override
    public OutGetActivityDto getActivity(Long id) throws Exception {
        OutGetActivityDto outGetActivityDto = null;
        RedActivity redActivity = new RedActivity();
        redActivity.setId(id);
        redActivity.setDelFlag("0");
        redActivity = redActivityMapper.selectOne(new QueryWrapper<>(redActivity));
        if (null != redActivity){
            outGetActivityDto = new OutGetActivityDto();
            BeanUtils.copyProperties(redActivity,outGetActivityDto);

            List<Long> taskIds = taskRelService.getTaskListByRedId(id);
            outGetActivityDto.setTaskIds(taskIds);

            List<RedAuthorization> authList = authorizationService.getAuthListByRedId(id);
            if (null != authList && authList.size() > 0) {
                InRedAuthDto redAuthDto = new InRedAuthDto();
                redAuthDto.setHasAuth(DictConstant.YES.equals(authList.get(0).getCanGrabFlag())?true:false);
                List<String> userAccounts = authList.stream().map(RedAuthorization::getUserAccount).collect(Collectors.toList());
                redAuthDto.setUserAccounts(userAccounts);
                outGetActivityDto.setUserAuth(redAuthDto);
            }
        }
        dictHandle.handleDict(outGetActivityDto);
        return outGetActivityDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(InBatchIdDto<Long> inBatchIdDto) throws Exception {
        redActivityMapper.deleteActivity(inBatchIdDto);
    }

    @Override
    public Page<OutQueryActivityDto> queryActivity(InQueryActivityDto inQueryActivityDto) throws Exception {
        Page page = new Page(inQueryActivityDto.getCurrent(),inQueryActivityDto.getSize());

        List<OutQueryActivityDto> activityDtoList = redActivityMapper.queryActivity(page,inQueryActivityDto);
        dictHandle.handleDict(activityDtoList);
        page.setRecords(activityDtoList);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(InAddActivityDto inAddActivityDto) throws Exception {
        long redActivityId = inAddActivityDto.getId();
        RedActivity redActivity = redActivityMapper.selectById(redActivityId);
        if (redActivity == null){
            throw new BusinessException("红包活动不存在");
        }
        if (DictConstant.ACTIVITY_STATUS_ACTIVE.equals(redActivity.getActivityStatus())){
            throw new BusinessException("已发布的活动不允许修改");
        }
        redActivity = new RedActivity();
        BeanUtils.copyProperties(inAddActivityDto,redActivity);
        redActivity.preUpdate("123");

        redActivityMapper.updateById(redActivity);

        authorizationService.deleteAuthOfRed(redActivityId);
        // 领取人员范围
        addAuth(inAddActivityDto,redActivityId);

        taskRelService.deleteTaskRelOfRed(redActivityId);
        // 前置任务
        addTaskRel(inAddActivityDto,redActivityId);
    }

    @Override
    public void activeActivity(InBatchIdDto<Long> inBatchIdDto) throws Exception {
        redActivityMapper.updateActivityStatus(inBatchIdDto,new Date(),DictConstant.ACTIVITY_STATUS_ACTIVE,"123");
    }

    @Override
    public void freezeActivity(InBatchIdDto<Long> inBatchIdDto) throws Exception {
        redActivityMapper.updateActivityStatus(inBatchIdDto,new Date(),DictConstant.ACTIVITY_STATUS_FREEZE,"123");
    }

    private void addAuth(InAddActivityDto inAddActivityDto,Long redActivityId) throws Exception{
        if (null != inAddActivityDto.getUserAuth()) {
            List<RedAuthorization> authList = new ArrayList<>();
            InRedAuthDto inRedAuthDto = inAddActivityDto.getUserAuth();
            String canGrabFlag = inRedAuthDto.isHasAuth() ? DictConstant.YES : DictConstant.NO;
            RedAuthorization redAuthorization;
            for (String userAccount : inRedAuthDto.getUserAccounts()) {
                redAuthorization = new RedAuthorization();
                redAuthorization.setId(idGenerator.next());
                redAuthorization.setRedId(redActivityId);
                redAuthorization.setCanGrabFlag(canGrabFlag);
                redAuthorization.setUserAccount(userAccount);
                authList.add(redAuthorization);
            }
            authorizationService.saveBatch(authList);
//            authorizationService.insertBatch(authList);
        }
    }

    private void addTaskRel(InAddActivityDto inAddActivityDto,Long redActivityId) throws Exception{
        if (null != inAddActivityDto.getTaskIds() && inAddActivityDto.getTaskIds().size() > 0){
            List<RedTaskRel> redTaskRelList = new ArrayList<>();
            RedTaskRel redTaskRel;
            for (Long taskId : inAddActivityDto.getTaskIds()){
                redTaskRel = new RedTaskRel();
                redTaskRel.setId(idGenerator.next());
                redTaskRel.setRedId(redActivityId);
                redTaskRel.setTaskId(taskId);
                redTaskRelList.add(redTaskRel);
            }
            taskRelService.saveBatch(redTaskRelList);
//            taskRelService.insertBatch(redTaskRelList);
        }
    }
}
