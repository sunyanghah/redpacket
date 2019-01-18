package com.tianrun.redpacket.imred.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.constant.RocketMqConstants;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.imred.dto.*;
import com.tianrun.redpacket.imred.entity.RedGrab;
import com.tianrun.redpacket.imred.entity.RedOrder;
import com.tianrun.redpacket.imred.mapper.RedGrabMapper;
import com.tianrun.redpacket.imred.mapper.RedOrderMapper;
import com.tianrun.redpacket.imred.service.RedGrabService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
@Service
@Slf4j
public class RedGrabServiceImpl implements RedGrabService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DefaultMQProducer producer;

    @Autowired
    private RedOrderMapper redOrderMapper;

    @Autowired
    private RedGrabMapper redGrabMapper;

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


    /**
     * 抢红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    @Override
    public OutGrabDto grab(String redNo, String userAccount) throws Exception {
        OutGrabDto outGrabDto = new OutGrabDto();
        try {
            // 获取红包信息
            List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                    Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_SIZE, RedConstants.HB_MONEY));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                String deadLine = list.get(0);
                int size = Integer.parseInt(list.get(1));
                int money = Integer.parseInt(list.get(2));

                // 判断红包是否过期
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("红包已经过期了");
                    return outGrabDto;
                }
                // 判断红包是否剩余
                if (size <= 0 || money <= 0) {
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("手慢了。红包已经被抢完了");
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
        outGrabDto.setMsg("红包已经过期了");
        return outGrabDto;
    }

    /**
     * 拆红包
     * @param redNo
     * @param userAccount
     * @return
     * @throws Exception
     */
    @Override
    public OutUnpackDto unpack(String redNo, String userAccount) throws Exception {
        OutUnpackDto outUnpackDto = new OutUnpackDto();
        try {
            // 获取红包信息
            List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                    Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_TYPE, RedConstants.HB_SIZE));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                // 判断红包是否过期
                String deadLine = list.get(0);
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("红包已经过期了");
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

    @Override
    public OutRedUnpackInfoDto getUnpackInfo(String redNo, String userAccount) throws Exception {
        OutRedUnpackInfoDto outRedUnpackInfoDto = new OutRedUnpackInfoDto();
        RedOrder redOrderQuery = new RedOrder();
        redOrderQuery.setRedNo(redNo);
        RedOrder redOrder = redOrderMapper.selectOne(new QueryWrapper<>(redOrderQuery));
        if (null == redOrder){
            throw new BusinessException("红包不存在");
        }

        RedGrab redGrabQuery = new RedGrab();
        redGrabQuery.setRedNo(redNo);
        List<RedGrab> redGrabList = redGrabMapper.selectList(new QueryWrapper<>(redGrabQuery));

        outRedUnpackInfoDto.setRedNo(redNo);
        outRedUnpackInfoDto.setRedType(redOrder.getRedType());
        outRedUnpackInfoDto.setRedContent(redOrder.getRedContent());
        outRedUnpackInfoDto.setSenderAccount(redOrder.getUserAccount());
        // TODO 用户姓名和头像
        outRedUnpackInfoDto.setSenderName(null);
        outRedUnpackInfoDto.setUnpackNum(redGrabList == null?0:redGrabList.size());
        outRedUnpackInfoDto.setRedNum(redOrder.getRedNum());
        if (redGrabList != null && redGrabList.size() > 0){
            List<RedUnpackInfoDto> unpackInfoList = new ArrayList<>();
            RedUnpackInfoDto redUnpackInfoDto;
            for (RedGrab redGrab : redGrabList){
                redUnpackInfoDto = new RedUnpackInfoDto();
                redUnpackInfoDto.setUserAccount(redGrab.getUserAccount());
                // TODO 用户姓名和头像
                redUnpackInfoDto.setUserName(null);
                redUnpackInfoDto.setUnpackMoney(redGrab.getMoney());
                redUnpackInfoDto.setUnpackTime(redGrab.getGrabTime());
                // TODO 运气王
                redUnpackInfoDto.setBestLuck(DictConstant.YES.equals(redGrab.getBestLuck())?true:false);
                if (userAccount.equals(redGrab.getUserAccount())){
                    outRedUnpackInfoDto.setMyUnpack(redGrab.getMoney());
                }
                unpackInfoList.add(redUnpackInfoDto);
            }
            outRedUnpackInfoDto.setUnpackInfoList(unpackInfoList);
        }
        return outRedUnpackInfoDto;
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
                unpackMessageDto.setRedSource(DictConstant.RED_SOURCE_IM);
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
            Message message = new Message(RocketMqConstants.RED_TOPIC, RocketMqConstants.TAGS_UNPACK,
                    RocketMqConstants.TAGS_UNPACK, JSON.toJSONBytes(unpackMessageDto));
            SendResult sendResult = producer.send(message);
            if (!RocketMqConstants.SEND_OK.equals(sendResult.getSendStatus().toString())) {
               throw new BusinessException(sendResult.getSendStatus().toString());
            }
        }catch (Exception e){
            // 记录发送失败的消息
            Map<String, Object> map = new HashMap<>();
            map.put("topic", RocketMqConstants.RED_TOPIC);
            map.put("tags", RocketMqConstants.TAGS_UNPACK);
            map.put("data", unpackMessageDto);
            redisTemplate.opsForList().leftPush(RocketMqConstants.ERROR_SEND, map);
            log.error("拆红包消息发送异常{}",e);
        }
    }

}
