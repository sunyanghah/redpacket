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
                    Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_SIZE));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                String deadLine = list.get(0);
                int size = Integer.parseInt(list.get(1));

                // 判断红包是否过期
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outGrabDto.setCanGrabFlag(false);
                    outGrabDto.setMsg("红包已经过期了");
                    return outGrabDto;
                }
                // 判断红包是否剩余
                if (size <= 0) {
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
                    Arrays.asList(RedConstants.HB_DEADLINE,RedConstants.HB_SIZE));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                // 判断红包是否过期
                String deadLine = list.get(0);
                if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("红包已经过期了");
                    return outUnpackDto;
                }
                int size = Integer.parseInt(list.get(1));
                // 判断红包是否剩余
                if (size <= 0) {
                    outUnpackDto.setCanUnpackFlag(false);
                    outUnpackDto.setMsg("手慢了。红包已经被抢完了");
                    return outUnpackDto;
                }
                //判断是否已抢过
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
                        // TODO 抢到的金额异步入余额
                        // TODO 给抢红包和发红包者，发送消息。
                        // TODO 最后一个红包被抢后，给发红包者发送被抢完消息，其他处理（redis中的数据怎么处理）
                        UnpackMessageDto unpackMessageDto = new UnpackMessageDto();
                        unpackMessageDto.setRedNo(redNo);
                        unpackMessageDto.setGrabTime(new Date());
                        unpackMessageDto.setRedSource(DictConstant.RED_SOURCE_IM);
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
