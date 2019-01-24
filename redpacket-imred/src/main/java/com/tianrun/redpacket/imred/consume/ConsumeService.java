package com.tianrun.redpacket.imred.consume;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.constant.RocketMqConstants;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.imred.dto.UnpackMessageDto;
import com.tianrun.redpacket.imred.entity.RedGrab;
import com.tianrun.redpacket.imred.entity.RedImDistribute;
import com.tianrun.redpacket.imred.entity.RedRefund;
import com.tianrun.redpacket.imred.mapper.RedGrabMapper;
import com.tianrun.redpacket.imred.mapper.RedImDistributeMapper;
import com.tianrun.redpacket.imred.mapper.RedRefundMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by dell on 2019/1/14.
 * @author dell
 */
@Component
@Slf4j
public class ConsumeService  {

    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private RedGrabMapper redGrabMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedRefundMapper redRefundMapper;
    @Autowired
    private RedImDistributeMapper redImDistributeMapper;
    @Autowired
    private DefaultMQProducer producer;

    /**
     * 拆红包消息消费
     * @param unpackMessageDto
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void unpackMessageHandle(UnpackMessageDto unpackMessageDto) throws Exception {
        addGrabNote(unpackMessageDto);
        if (unpackMessageDto.isLastOne()){
            RedImDistribute redImDistribute = new RedImDistribute();
            redImDistribute.setRedNo(unpackMessageDto.getRedNo());
            redImDistribute = redImDistributeMapper.selectOne(new QueryWrapper<>(redImDistribute));
            if (redImDistribute != null && DictConstant.RED_TYPE_LUCK.equals(redImDistribute.getRedType())) {
                setBestLuck(unpackMessageDto.getRedNo());
            }
        }
    }

    /**
     * 过期红包消息消费
     * @param redNo
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void expireMessageHandle(String redNo) throws Exception {
        try {
            List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                    Arrays.asList(RedConstants.HB_SIZE));
            if (null != list && list.size() > 0 && !list.contains(null)) {
                int size = Integer.parseInt(list.get(0));
                if (size > 0) {
                    List<String> moneyList = redisTemplate.opsForList().range(RedConstants.HB_MONEY_LIST + redNo, 0, -1);
                    if (moneyList != null && moneyList.size() > 0 && moneyList.size() == size) {
                        Integer remainMoney = 0;
                        for (String remain : moneyList) {
                            int price = 0;
                            try {
                                if (remain != null) {
                                    price = Integer.parseInt(remain);
                                }
                            } catch (Exception e) {
                                price = 0;
                            }
                            remainMoney += price;
                        }

                        addRefundRecord(remainMoney, redNo);

                    }
                }
            } else {
                sendMqForExpireByDB(redNo);
            }
        }catch (Exception e){
            sendMqForExpireByDB(redNo);
        }
        System.out.println(redNo+"过期了-------------------------------------"+System.currentTimeMillis());
    }

    /**
     * 如果redis挂了，获取不到剩余金额信息。则往后延时一定时间交由查数据库。
     * 延时的目的是为了让抢红包记录的消息尽可能的消费完成
     * @param redNo
     */
    private void sendMqForExpireByDB(String redNo) {
        try {
            Message message = new Message(RocketMqConstants.RED_TOPIC, RocketMqConstants.TAGS_EXPIRE_DB,
                    RocketMqConstants.TAGS_EXPIRE_DB, redNo.getBytes());
            message.setDelayTimeLevel(RocketMqConstants.ROCKET_DELAY_LEVEL_9);
            SendResult sendResult = producer.send(message);
            if (!RocketMqConstants.SEND_OK.equals(sendResult.getSendStatus().toString())) {
                throw new BusinessException(sendResult.getSendStatus().toString());
            }
        }catch (Exception e){
            Map<String, Object> map = new HashMap<>();
            map.put("topic", RocketMqConstants.RED_TOPIC);
            map.put("tags", RocketMqConstants.TAGS_EXPIRE);
            map.put("data", redNo);
            redisTemplate.opsForList().leftPush(RocketMqConstants.ERROR_SEND, map);
            log.error("红包过期转交数据库处理发送失败 红包编号 {},异常信息{}", redNo,e);
        }
    }

    public void handleExpireByRedisDown(String redNo) throws Exception{
        log.info("----------redis是不是死了啊-----------");
        Integer grabMoney = redGrabMapper.getGrabMoneyByRedNo(redNo);
        grabMoney = grabMoney == null ? 0:grabMoney;
        RedImDistribute redImDistribute = new RedImDistribute();
        redImDistribute.setRedNo(redNo);
        redImDistribute = redImDistributeMapper.selectOne(new QueryWrapper<>(redImDistribute));
        if (redImDistribute != null && redImDistribute.getRedMoney() != null) {
            Integer remainMoney = redImDistribute.getRedMoney() - grabMoney;
            addRefundRecord(remainMoney,redNo);
        }
    }

    private void addRefundRecord(Integer remainMoney,String redNo) throws Exception{
        if (remainMoney > 0){
            RedImDistribute redImDistributeQuery = new RedImDistribute();
            redImDistributeQuery.setRedNo(redNo);
            RedImDistribute redImDistribute = redImDistributeMapper.selectOne(new QueryWrapper<>(redImDistributeQuery));
            StringBuilder remarks = new StringBuilder("发送至");
            if (DictConstant.RED_OBJECT_PERSONAL.equals(redImDistribute.getRedObject())){
                remarks.append("好友\"").append(redImDistribute.getObjectName()).append("\"");
            }
            if (DictConstant.RED_OBJECT_GROUP.equals(redImDistribute.getRedObject())){
                remarks.append("群组\"").append(redImDistribute.getObjectName()).append("\"");
            }
            remarks.append("的红包超时未领取");
            RedRefund redRefund = new RedRefund();
            redRefund.setId(idGenerator.next());
            redRefund.setRedNo(redNo);
            redRefund.setRefundWay("退回零钱");
            redRefund.setRefundMoney(remainMoney);
            redRefund.setRefundReason("红包超时未领退回");
            redRefund.setRefundTime(new Date());
            redRefund.setRemarks(remarks.toString());
            redRefundMapper.insert(redRefund);
            //TODO 退回红包发送人的余额

            //TODO 删除redis中的数据 hbInfo 和 hbUser
        }
    }

    /**
     * 拆红包记录异步入库
     * @param unpackMessageDto
     * @throws Exception
     */
    private void addGrabNote(UnpackMessageDto unpackMessageDto) throws Exception {
        RedGrab redGrab = new RedGrab();
        redGrab.setId(idGenerator.next());
        redGrab.setRedNo(unpackMessageDto.getRedNo());
        redGrab.setMoney(unpackMessageDto.getMoney());
        redGrab.setUserAccount(unpackMessageDto.getUserAccount());
        redGrab.setGrabTime(unpackMessageDto.getGrabTime());
        redGrab.setRedSource(unpackMessageDto.getRedSource());
        redGrab.setBestLuck(DictConstant.NO);
        redGrabMapper.insert(redGrab);
    }

    /**
     * 设置手气最佳
     * @param redNo
     * @throws Exception
     */
    private void setBestLuck(String redNo) throws Exception {
        RedGrab bestLuckRedGrab = redGrabMapper.getBestLuckRedGrab(redNo);
        if (bestLuckRedGrab != null){
            bestLuckRedGrab.setBestLuck(DictConstant.YES);
            redGrabMapper.updateById(bestLuckRedGrab);
        }
    }

}
