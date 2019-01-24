package com.tianrun.redpacket.companyred.service.impl;

import com.alibaba.fastjson.JSON;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.constant.RocketMqConstants;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.companyred.dto.OutGrabDto;
import com.tianrun.redpacket.companyred.dto.OutUnpackDto;
import com.tianrun.redpacket.companyred.dto.UnpackMessageDto;
import com.tianrun.redpacket.companyred.entity.RedAuthorization;
import com.tianrun.redpacket.companyred.service.GrabService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dell on 2019/1/21.
 * @author dell
 */
@Service
@Slf4j
public class GrabServiceImpl implements GrabService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultMQProducer producer;


    @Override
    public OutGrabDto grab(String redNo, String userAccount) throws Exception{
        OutGrabDto outGrabDto = new OutGrabDto();
        try {
            // 获取红包信息
            List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                    Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_SIZE,RedConstants.HB_STATUS));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                String deadLine = list.get(0);
                int size = Integer.parseInt(list.get(1));
                String status = list.get(2);
                // 判断红包是否过期
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("活动已经过期了");
                    return outGrabDto;
                }
                // 判断红包状态
                if (!DictConstant.ACTIVITY_STATUS_ACTIVE.equals(status)){
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("活动未激活或已冻结");
                    return outGrabDto;
                }
                // 判断红包是否剩余
                if (size <= 0 ) {
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("手慢了。红包已经被抢完了");
                    return outGrabDto;
                }
                // 检查领取权限情况
                if (!checkAuth(redNo, userAccount)){
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("无领取权限");
                    return outGrabDto;
                }
                // 检查任务完成情况
                if (!checkTask(redNo, userAccount)){
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("未完成活动任务");
                    return outGrabDto;
                }

                outGrabDto.setCanGrabFlag(true);
                outGrabDto.setMsg("快拆红包吧");
                return outGrabDto;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        outGrabDto.setCanGrabFlag(false);
        outGrabDto.setMsg("活动已经过期了");
        return outGrabDto;
    }

    private boolean checkAuth(String redNo,String userAccount) throws Exception{
        // 获取领取人员信息
        List<String> list = redisTemplate.opsForList().range(RedConstants.HB_AUTH+redNo,0,-1);
        if (list != null && list.size() > 0){
            // 当前设置的是可抢人员还是不可抢人员
            String authMode = null;
            for (String jsonStr : list){
                RedAuthorization redAuthorization = JSON.parseObject(jsonStr, RedAuthorization.class);
                // 获取可抢还是不可抢模式
                if (authMode == null){
                    authMode = redAuthorization.getCanGrabFlag();
                }
                // 此人在人员名单中时
                if (userAccount.equals(redAuthorization.getUserAccount())) {
                    // 如果设置的可抢，则返回可抢
                    if (DictConstant.YES.equals(authMode)){
                        return true;
                    // 否则不可抢
                    }else{
                        return false;
                    }
                }
            }
            // 可抢模式下，如果不在人员名单中，则不可抢
            if (DictConstant.YES.equals(authMode)){
                return false;
            // 不可抢模式下，如果不在名单中 ，则可抢
            }else{
                return true;
            }
        // 如果为空 则未设置，未设置时都可抢
        }else {
            return true;
        }
    }

    private boolean checkTask(String redNo, String userAccount) throws Exception{
        List<String> taskList = redisTemplate.opsForList().range(RedConstants.HB_TASK+redNo,0,-1);
        if (taskList != null && taskList.size() > 0){
            // 如果设置了前置任务
            if (DictConstant.YES.equals(taskList.get(0))){
                List<String> taskUserList = taskList.subList(1,taskList.size());
                // 如果有人完成
                if (taskUserList != null && taskUserList.size() > 0){
                    // 如果此人不在完成的人里，则不可抢
                    if (!taskUserList.contains(userAccount)){
                        return false;
                    }
                // 如果没人完成，则不可抢
                }else{
                    return false;
                }
            // 如果没设置任务则可抢
            }else{
                return true;
            }
        }
        // 默认可抢
        return true;
    }

    @Override
    public OutUnpackDto unpack(String redNo, String userAccount) throws Exception {
        OutUnpackDto outUnpackDto = new OutUnpackDto();
        try {
            // 获取红包信息
            List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                    Arrays.asList(RedConstants.HB_DEADLINE,  RedConstants.HB_SIZE, RedConstants.HB_STATUS));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                // 判断红包是否过期
                String deadLine = list.get(0);
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("红包已经过期了");
                    return outUnpackDto;
                }
                int size = Integer.parseInt(list.get(1));
                if (size <= 0){
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("手慢了。红包已经被抢完了");
                    return outUnpackDto;
                }
                String status = list.get(2);
                // 判断红包状态
                if (!DictConstant.ACTIVITY_STATUS_ACTIVE.equals(status)){
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("活动未激活或已冻结");
                    return outUnpackDto;
                }
                // 检查领取权限情况
                if (!checkAuth(redNo, userAccount)){
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("无领取权限");
                    return outUnpackDto;
                }
                // 检查任务完成情况
                if (!checkTask(redNo, userAccount)){
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("未完成活动任务");
                    return outUnpackDto;
                }
                if (checkAlreadyUnpack(redNo,userAccount)){
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("你已经抢过红包了，不能再抢了");
                    return outUnpackDto;
                }

                Object object = redisTemplate.opsForList().leftPop(RedConstants.HB_MONEY_LIST+redNo);

                try {
                    Integer money = Integer.parseInt(object.toString());
                    if (money <= 0){
                        throw new BusinessException();
                    }
                    try {
                        redisTemplate.opsForHash().increment(RedConstants.HB_INFO+redNo,RedConstants.HB_SIZE,-1);
                        redisTemplate.opsForHash().put(RedConstants.HB_USER + redNo, userAccount, String.valueOf(money));

                        // TODO 拆红包记录异步入库
                        // TODO 抢到的金额异步入余额,或者场景余额
                        UnpackMessageDto unpackMessageDto = new UnpackMessageDto();
                        unpackMessageDto.setRedNo(redNo);
                        unpackMessageDto.setGrabTime(new Date());
                        unpackMessageDto.setRedSource(DictConstant.RED_SOURCE_COMPANY);
                        unpackMessageDto.setUserAccount(userAccount);
                        unpackMessageDto.setMoney(money);
                        boolean lastOne = size == 1 ? true : false;
                        unpackMessageDto.setLastOne(lastOne);
                        sendMqForUnpack(unpackMessageDto);

                        outUnpackDto.setCanUnpackFlag(true);
                        outUnpackDto.setMsg("恭喜你，抢到了");
                        outUnpackDto.setMoney(money);
                        return outUnpackDto;
                    }catch (Exception e){
                        redisTemplate.opsForHash().increment(RedConstants.HB_INFO+redNo,RedConstants.HB_SIZE,1);
                        redisTemplate.opsForList().rightPush(RedConstants.HB_MONEY_LIST+redNo,money);
                    }
                }catch (Exception e){
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("手慢了。红包已经被抢完了");
                    return outUnpackDto;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        outUnpackDto.setCanUnpackFlag(false);
        outUnpackDto.setMsg("红包已经过期了");
        return outUnpackDto;
    }

    private boolean checkAlreadyUnpack(String redNo,String userAccount) throws Exception{
        return redisTemplate.opsForHash().hasKey(RedConstants.HB_USER+redNo,userAccount);
    }


    /**
     * 发送mq消息，异步处理拆红包后的操作
     * @param unpackMessageDto
     */
    private void sendMqForUnpack(UnpackMessageDto unpackMessageDto){
        try {
            Message message = new Message(RocketMqConstants.ACTIVITY_TOPIC, RocketMqConstants.TAGS_UNPACK,
                    RocketMqConstants.TAGS_UNPACK, JSON.toJSONBytes(unpackMessageDto));
            SendResult sendResult = producer.send(message);
            if (!RocketMqConstants.SEND_OK.equals(sendResult.getSendStatus().toString())) {
                throw new BusinessException(sendResult.getSendStatus().toString());
            }
        }catch (Exception e){
            // 记录发送失败的消息
            Map<String, Object> map = new HashMap<>();
            map.put("topic", RocketMqConstants.ACTIVITY_TOPIC);
            map.put("tags", RocketMqConstants.TAGS_UNPACK);
            map.put("data", unpackMessageDto);
            redisTemplate.opsForList().leftPush(RocketMqConstants.ERROR_SEND, map);
            log.error("拆红包消息发送异常{}",e);
        }
    }
}
