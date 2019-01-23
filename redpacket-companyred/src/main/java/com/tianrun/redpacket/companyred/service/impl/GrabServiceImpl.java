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
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

    private static DefaultRedisScript redLuckScript = new DefaultRedisScript();
    private static DefaultRedisScript redNormalScript = new DefaultRedisScript();
    static {
        redLuckScript.setResultType(String.class);
        redLuckScript.setScriptText("math.randomseed(KEYS[6]:reverse():sub(1,6)) \n" +
                "local function getRandomMoney( remainSize, remainMoney, minPrice, maxPrice) \n" +
                "if (remainSize <=0 or remainMoney <=0 ) then \n" +
                "return nil \n" +
                "end \n" +
                "if (minPrice == 0 or minPrice == nil) then \n" +
                "minPrice = 1 \n" +
                "end \n" +
                "if (maxPrice == 0 or maxPrice == nil) then \n" +
                "maxPrice = remainMoney \n" +
                "end \n" +
                "local minTemp = remainMoney - (maxPrice * (remainSize-1)) \n" +
                "local randomMin = minTemp \n" +
                "if (minTemp < minPrice) then \n" +
                "randomMin = minPrice \n" +
                "end \n" +
                "local maxTemp = remainMoney - (minPrice * (remainSize-1)) \n" +
                "local randomMax = maxTemp  \n" +
                "if (maxTemp > maxPrice) then \n" +
                "randomMax = maxPrice \n" +
                "end \n" +
                "local beatMax = remainMoney / remainSize * 2 \n" +
                "if (randomMax > beatMax) then \n" +
                "randomMax = beatMax \n" +
                "end \n" +
                "local beatMin = remainMoney / remainSize / 2 \n" +
                "if (randomMin < beatMin) then \n" +
                "randomMin = beatMin \n" +
                "end \n"+
                "return math.floor((math.random()*(randomMax-randomMin) + randomMin)+0.5); \n" +
                "end \n" +
                "if redis.call('hexists', KEYS[7], KEYS[8]) ~= 0 then \n" +
                "return nil\n" +
                "else\n" +
                "local size = tonumber(redis.call('hget', KEYS[1], KEYS[2])) \n" +
                "local money = tonumber(redis.call('hget', KEYS[1], KEYS[3])) \n" +
                "local maxPrice = tonumber(redis.call('hget', KEYS[1], KEYS[4])) \n" +
                "local minPrice = tonumber(redis.call('hget', KEYS[1], KEYS[5])) \n" +
                "if (size <= 0 or money <= 0)then \n" +
                "return tostring(0) \n" +
                "else \n" +
                "local randomMoney = getRandomMoney( size, money,minPrice,maxPrice) \n" +
                "redis.call('hincrby', KEYS[1], KEYS[2], -1) \n" +
                "redis.call('hincrbyfloat', KEYS[1], KEYS[3], -randomMoney) \n" +
                "redis.call('hmset', KEYS[7],KEYS[8],randomMoney) \n" +
                "return tostring(randomMoney) \n" +
                "end\n" +
                "end");


        redNormalScript.setResultType(String.class);
        redNormalScript.setScriptText("if redis.call('hexists', KEYS[5], KEYS[6]) ~= 0 then \n" +
                "return nil\n" +
                "else\n" +
                "local size = tonumber(redis.call('hget', KEYS[1], KEYS[2])) \n" +
                "local money = tonumber(redis.call('hget', KEYS[1], KEYS[3])) \n" +
                "local price = tonumber(redis.call('hget', KEYS[1], KEYS[4])) \n" +
                "if (size <= 0 or money <= 0)then \n" +
                "return tostring(0) \n" +
                "else  \n" +
                "redis.call('hincrby', KEYS[1], KEYS[2], -1) \n" +
                "redis.call('hincrbyfloat', KEYS[1], KEYS[3], -price) \n" +
                "redis.call('hmset', KEYS[5],KEYS[6],price) \n" +
                "return tostring(price) \n" +
                "end\n" +
                "end");
    }

    @Override
    public OutGrabDto grab(String redNo, String userAccount) throws Exception{
        OutGrabDto outGrabDto = new OutGrabDto();
        try {
            // 获取红包信息
            List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                    Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_SIZE, RedConstants.HB_MONEY,RedConstants.HB_STATUS));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                String deadLine = list.get(0);
                int size = Integer.parseInt(list.get(1));
                int money = Integer.parseInt(list.get(2));
                String status = list.get(3);
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
                if (size <= 0 || money <= 0) {
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
                    Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_TYPE, RedConstants.HB_SIZE, RedConstants.HB_STATUS));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                // 判断红包是否过期
                String deadLine = list.get(0);
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("红包已经过期了");
                    return outUnpackDto;
                }
                String status = list.get(3);
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
                String type = list.get(1);
                // 拼手气红包
                if (DictConstant.RED_TYPE_LUCK.equals(type)) {
                    // 判断红包是否最后一个
                    int size = Integer.parseInt(list.get(2));
                    boolean lastOne = size == 1?true:false;
                    // 执行lua脚本
                    Object object = redisTemplate.execute(redLuckScript,
                            Arrays.asList(RedConstants.HB_INFO + redNo, RedConstants.HB_SIZE,
                                    RedConstants.HB_MONEY, RedConstants.HB_MAX_PRICE, RedConstants.HB_MIN_PRICE,
                                    String.valueOf(System.currentTimeMillis()), RedConstants.HB_USER + redNo, userAccount));
                    return handleUnpackResult(outUnpackDto,object,userAccount,redNo,lastOne);
                    // 普通平分红包
                } else if (DictConstant.RED_TYPE_NORMAL.equals(type)) {
                    // 执行lua脚本
                    Object object = redisTemplate.execute(redNormalScript,
                            Arrays.asList(RedConstants.HB_INFO + redNo, RedConstants.HB_SIZE,
                                    RedConstants.HB_MONEY, RedConstants.HB_PRICE,
                                    RedConstants.HB_USER + redNo, userAccount));
                    return handleUnpackResult(outUnpackDto,object,userAccount,redNo,false);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        outUnpackDto.setCanUnpackFlag(false);
        outUnpackDto.setMsg("红包已经过期了");
        return outUnpackDto;
    }

    /**
     * 处理脚本执行结果
     * @param outUnpackDto
     * @param object
     * @param userAccount
     * @param redNo
     * @param lastOne
     * @return
     * @throws Exception
     */
    private OutUnpackDto handleUnpackResult(OutUnpackDto outUnpackDto,Object object,
                                            String userAccount,String redNo,boolean lastOne) throws Exception{
        // 处理重复拆包
        if (null == object) {
            outUnpackDto.setCanUnpackFlag(false);
            outUnpackDto.setMsg("你已经抢过红包了，不能再抢了");
            return outUnpackDto;
        } else {
            // 判断红包是否剩余
            int redMoney = Integer.parseInt(object.toString());
            if (redMoney == 0) {
                outUnpackDto.setCanUnpackFlag(false);
                outUnpackDto.setMsg("手慢了，红包已经被抢完了");
                return outUnpackDto;
            } else {
                // TODO 拆红包记录异步入库
                // TODO 抢到的金额异步入余额
                // TODO 给抢红包和发红包者，发送消息。
                // TODO 最后一个红包被抢后，给发红包者发送被抢完消息，其他处理（redis中的数据怎么处理）
                UnpackMessageDto unpackMessageDto = new UnpackMessageDto();
                unpackMessageDto.setRedNo(redNo);
                unpackMessageDto.setGrabTime(new Date());
                unpackMessageDto.setRedSource(DictConstant.RED_SOURCE_COMPANY);
                unpackMessageDto.setUserAccount(userAccount);
                unpackMessageDto.setMoney(redMoney);
                unpackMessageDto.setLastOne(lastOne);
                sendMqForUnpack(unpackMessageDto);

                outUnpackDto.setCanUnpackFlag(true);
                outUnpackDto.setMsg("恭喜你，抢到了");
                outUnpackDto.setMoney(redMoney);
                return outUnpackDto;
            }
        }
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
