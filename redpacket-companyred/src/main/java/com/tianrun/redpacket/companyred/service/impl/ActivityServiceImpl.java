package com.tianrun.redpacket.companyred.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.dict.DictHandle;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.common.util.RedUtil;
import com.tianrun.redpacket.companyred.dto.*;
import com.tianrun.redpacket.companyred.entity.*;
import com.tianrun.redpacket.companyred.mapper.RedActivityMapper;
import com.tianrun.redpacket.companyred.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Autowired
    private ActivityPlaceService activityPlaceService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TaskResultService taskResultService;

    /**
     * 检查设置的红包金额是否合理
     * 单位 分。
     * @param redAmount
     * @param redNum
     * @param maxPrice
     * @return
     * @throws Exception
     */
    private void checkPrice(Integer redAmount,Integer redNum,Integer maxPrice) throws Exception {

        if (redAmount == null || redAmount == 0){
            throw new BusinessException("红包总金额不能为0");
        }
        if (redNum == null || redNum == 0){
            throw new BusinessException("红包领取人数不能为0");
        }

        Integer averPrice = redAmount/redNum;
        if (averPrice < 1){
            throw new BusinessException("请确保至少每人领到0.01元");
        }

        if (maxPrice != null && maxPrice != 0) {
            if (averPrice >= maxPrice){
                throw new BusinessException("最大领取金额必须大于平均领取金额");
            }
        }
    }

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
        redActivity.setRedNo(String.valueOf(idGenerator.next()));
        redActivity.preInsert("123");
        redActivity.setActivityStatus(DictConstant.ACTIVITY_STATUS_UNACTIVE);
        if (DictConstant.RED_TYPE_NORMAL.equals(redActivity.getRedType())){
            if (null == redActivity.getRedPrice() || redActivity.getRedPrice() == 0){
                throw new BusinessException("普通红包的单个红包金额不能为0");
            }
            redActivity.setRedAmount(redActivity.getRedNum()*redActivity.getRedPrice());
        }
        checkPrice(redActivity.getRedAmount(),redActivity.getRedNum(),redActivity.getMaxPrice());
        redActivityMapper.insert(redActivity);

        // 领取人员范围
        addAuth(inAddActivityDto,redActivityId);

        // 前置任务
        addTaskRel(inAddActivityDto,redActivityId);

        // 场景
        addPlace(inAddActivityDto.getPlaceList(),redActivityId);
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

            List<ActivityPlaceDto> placeDtoList = activityPlaceService.getPlaceListByRedId(id);
            outGetActivityDto.setPlaceList(placeDtoList);
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
        if (DictConstant.RED_TYPE_NORMAL.equals(redActivity.getRedType())){
            if (null == redActivity.getRedPrice() || redActivity.getRedPrice() == 0){
                throw new BusinessException("普通红包的单个红包金额不能为0");
            }
            redActivity.setRedAmount(redActivity.getRedNum()*redActivity.getRedPrice());
        }
        checkPrice(redActivity.getRedAmount(),redActivity.getRedNum(),redActivity.getMaxPrice());
        redActivityMapper.updateById(redActivity);

        // 领取人员范围
        authorizationService.deleteAuthOfRed(redActivityId);
        addAuth(inAddActivityDto,redActivityId);

        // 前置任务
        taskRelService.deleteTaskRelOfRed(redActivityId);
        addTaskRel(inAddActivityDto,redActivityId);

        // 场景
        activityPlaceService.deletePlaceOfRed(redActivityId);
        addPlace(inAddActivityDto.getPlaceList(),redActivityId);
    }

    /**
     * 激活红包活动
     * @param redId
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activeActivity(Long redId) throws Exception {
        // 获取活动集合
        RedActivity activity = redActivityMapper.selectById(redId);
        if (activity != null){
            // 如果之前是未激活
            if (DictConstant.ACTIVITY_STATUS_UNACTIVE.equals(activity.getActivityStatus())){
                addHbMoneyCache(activity);
                addHbInfoCache(activity);
                addAuthCache(activity);
                addTaskCache(activity);

            // 如果之前是冻结
            }else if (DictConstant.ACTIVITY_STATUS_FREEZE.equals(activity.getActivityStatus())){
                changeHbStatus(activity.getRedNo(),DictConstant.ACTIVITY_STATUS_ACTIVE);
            }
        }
        // 修改活动状态
        redActivityMapper.updateActivityStatus(Arrays.asList(redId),new Date(),DictConstant.ACTIVITY_STATUS_ACTIVE,"123");
    }

    private void changeHbStatus(String redNo,String status) throws Exception{
        redisTemplate.opsForHash().put(RedConstants.HB_INFO+redNo,RedConstants.HB_STATUS,status);
    }

    /**
     * 红包金额预生成
     * @param activity
     * @throws Exception
     */
    private void addHbMoneyCache(RedActivity activity) throws Exception {
        if (activity != null) {
            List<String> moneyList = new ArrayList<>();
            if (DictConstant.RED_TYPE_LUCK.equals(activity.getRedType())){
                moneyList = RedUtil.getRandomRed(activity.getRedNum(), activity.getRedAmount(), null,
                        activity.getMaxPrice(), String.class);
            }else if (DictConstant.RED_TYPE_NORMAL.equals(activity.getRedType())){
                for (int i=0;i<activity.getRedNum();i++){
                    moneyList.add(String.valueOf(activity.getRedPrice()));
                }
            }
            if (moneyList != null && moneyList.size() > 0){
                redisTemplate.opsForList().leftPushAll(RedConstants.HB_MONEY_LIST+activity.getRedNo(),moneyList);
            }
        }
    }

    /**
     * 红包基本信息入缓存
     * @param redActivity
     * @throws Exception
     */
    private void addHbInfoCache(RedActivity redActivity) throws Exception{
        // 红包信息放入缓存
        Map<String,String> hbMap = new HashMap<>();
        // 红包个数
        hbMap.put(RedConstants.HB_SIZE,String.valueOf(redActivity.getRedNum()));
        // 红包过期时间
        hbMap.put(RedConstants.HB_DEADLINE,String.valueOf(redActivity.getEndTime().getTime()));
        // 红包状态
        hbMap.put(RedConstants.HB_STATUS,DictConstant.ACTIVITY_STATUS_ACTIVE);
        redisTemplate.opsForHash().putAll(RedConstants.HB_INFO+redActivity.getRedNo(),hbMap);
    }

    /**
     * 领取人员范围放入缓存
     * @param redActivity
     * @throws Exception
     */
    private void addAuthCache(RedActivity redActivity) throws Exception {
        List<RedAuthorization> redAuthorizations = authorizationService.getAuthListByRedId(redActivity.getId());
        if (redAuthorizations != null && redAuthorizations.size() > 0){
            redAuthorizations.forEach(redAuthorization ->
                    redisTemplate.opsForList().leftPush(RedConstants.HB_AUTH+redActivity.getRedNo(), JSON.toJSONString(redAuthorization)));
        }
    }

    /**
     * 完成任务的人放入缓存
     * @param redActivity
     * @throws Exception
     */
    private void addTaskCache(RedActivity redActivity) throws Exception {
        List<RedTask> redTaskList = taskRelService.getTaskInfoByRedId(redActivity.getId());
        if (redTaskList != null && redTaskList.size() > 0){
            redisTemplate.opsForList().rightPush(RedConstants.HB_TASK+redActivity.getRedNo(),DictConstant.YES);
            List<String> accountList = taskResultService.getAccountByFinishAllTask(redTaskList,redTaskList.size());
            if (accountList != null && accountList.size() > 0){
                accountList.forEach(account ->
                        redisTemplate.opsForList().rightPush(RedConstants.HB_TASK+redActivity.getRedNo(),account));
            }

        }else{
            redisTemplate.opsForList().rightPush(RedConstants.HB_TASK+redActivity.getRedNo(),DictConstant.NO);
        }
    }


    /**
     * 冻结红包活动
     * @param redId
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeActivity(Long redId) throws Exception {
        // 获取活动集合
        RedActivity activity = redActivityMapper.selectById(redId);
        if (activity != null){
            // 如果之前是已激活
            if (DictConstant.ACTIVITY_STATUS_ACTIVE.equals(activity.getActivityStatus())){
                changeHbStatus(activity.getRedNo(),DictConstant.ACTIVITY_STATUS_FREEZE);
                //修改活动状态
                redActivityMapper.updateActivityStatus(Arrays.asList(redId),new Date(),DictConstant.ACTIVITY_STATUS_FREEZE,"123");
            }
        }

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
        }
    }

    private void addPlace(List<ActivityPlaceDto> placeList,Long redActivityId) throws Exception {
        if (placeList != null && placeList.size() > 0){
            List<RedActivityPlace> redActivityPlaceList = new ArrayList<>();
            RedActivityPlace redActivityPlace;
            for (ActivityPlaceDto activityPlaceDto : placeList){
                redActivityPlace = new RedActivityPlace();
                redActivityPlace.setId(idGenerator.next());
                redActivityPlace.setActivityId(redActivityId);
                redActivityPlace.setPlaceCode(activityPlaceDto.getPlaceCode());
                redActivityPlace.setPlaceName(activityPlaceDto.getPlaceName());
                redActivityPlaceList.add(redActivityPlace);
            }
            activityPlaceService.saveBatch(redActivityPlaceList);
        }
    }
}
