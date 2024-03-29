package com.tianrun.redpacket.imred.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.constant.RocketMqConstants;
import com.tianrun.redpacket.common.dto.OutRedConfigDto;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.common.util.RedUtil;
import com.tianrun.redpacket.imred.client.RedConfigClient;
import com.tianrun.redpacket.imred.dto.InRedPackDto;
import com.tianrun.redpacket.imred.dto.InRedPayFallbackDto;
import com.tianrun.redpacket.imred.dto.OutRedPackDto;
import com.tianrun.redpacket.imred.entity.RedImDistribute;
import com.tianrun.redpacket.imred.entity.RedOrder;
import com.tianrun.redpacket.imred.mapper.RedImDistributeMapper;
import com.tianrun.redpacket.imred.mapper.RedOrderMapper;
import com.tianrun.redpacket.imred.service.RedSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Service
@Slf4j
public class RedSendServiceImpl implements RedSendService {

    @Autowired
    private RedOrderMapper redOrderMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedImDistributeMapper redImDistributeMapper;
    @Autowired
    private DefaultMQProducer producer;
    @Autowired
    private RedConfigClient redConfigClient;

    /**
     * 检查设置的红包金额是否合理
     * 单位 分。
     * @param redAmount
     * @param redNum
     * @return
     * @throws Exception
     */
    private void checkPrice(Integer redAmount,Integer redNum) throws Exception {

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

    }

    /**
     * 监测红包金额是否超出设定的最大金额
     * 由于配置的特殊性，库里存的单位为元
     * 这里分转为元尚不存在精度问题
     * @param redObject
     * @param redAmount
     * @throws Exception
     */
    private void checkMaxConfig(String redObject,Integer redAmount) throws Exception{
        Double redAmountYuan = redAmount/100.0;
        if (DictConstant.RED_OBJECT_PERSONAL.equals(redObject)){
            //元
            Double personalMax = 200D;
            RP<OutRedConfigDto> personalMaxRp = redConfigClient.getConfigByCode(RedConstants.CONFIG_PERSONAL_MAX);
            if (personalMaxRp != null && personalMaxRp.getData() != null
                    && StringUtils.isNotBlank(personalMaxRp.getData().getConfigValue())){
                personalMax = new Double(personalMaxRp.getData().getConfigValue());
            }
            if (redAmountYuan.compareTo(personalMax) == 1){
                throw new BusinessException("单聊红包的最大红包金额不得超过"+personalMax+"元");
            }

        }else if (DictConstant.RED_OBJECT_GROUP.equals(redObject)){
            //元
            Double groupMax = 200D;
            RP<OutRedConfigDto> groupMaxRp = redConfigClient.getConfigByCode(RedConstants.CONFIG_GROUP_MAX);
            if (groupMaxRp != null && groupMaxRp.getData() != null
                    && StringUtils.isNotBlank(groupMaxRp.getData().getConfigValue())){
                groupMax = new Double(groupMaxRp.getData().getConfigValue());
            }
            if (redAmountYuan.compareTo(groupMax) == 1){
                throw new BusinessException("群聊红包的最大红包金额不得超过"+groupMax+"元");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutRedPackDto redPack(InRedPackDto inRedPackDto) throws Exception {
        OutRedPackDto outRedPackDto = new OutRedPackDto();

        RedOrder redOrder = new RedOrder();
        BeanUtils.copyProperties(inRedPackDto,redOrder);
        if (DictConstant.RED_TYPE_NORMAL.equals(redOrder.getRedType())){
            if (null == redOrder.getRedPrice() || redOrder.getRedPrice() == 0){
                throw new BusinessException("普通红包的单个红包金额不能为0");
            }
            redOrder.setRedMoney(redOrder.getRedNum() * redOrder.getRedPrice());
        }

        String redOrderNo = String.valueOf(idGenerator.next());

        checkPrice(redOrder.getRedMoney(),inRedPackDto.getRedNum());
        checkMaxConfig(redOrder.getRedObject(),inRedPackDto.getRedMoney());

        redOrder.setId(idGenerator.next());
        redOrder.setRedNo(redOrderNo);
        redOrder.setDistributeTime(new Date());
        redOrder.setStatus("1");
        // TODO 如果发送对服务器压力也很大，则这里放入缓存，不存库。支付回调里入库并删掉缓存里的数据。
        redOrderMapper.insert(redOrder);
        outRedPackDto.setWaitFor("备用字段");

        // TODO 这里模拟已经支付成功的回调，对接支付后修改
        InRedPayFallbackDto inRedPayFallbackDto = new InRedPayFallbackDto();
        inRedPayFallbackDto.setStatus("SUCCESS");
        inRedPayFallbackDto.setRequestNo(redOrderNo);
        inRedPayFallbackDto.setPayTool("余额支付");
        payFallback(inRedPayFallbackDto);
        ///////

        return outRedPackDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payFallback(InRedPayFallbackDto inRedPayFallbackDto) throws Exception {
        if ("SUCCESS".equals(inRedPayFallbackDto.getStatus())){

            long deadlineMillisecond = RedConstants.HB_DEADLINE_MILLISECOND;
            //获取红包过期时间
            RP<OutRedConfigDto> redConfigRp = redConfigClient.getConfigByCode(RedConstants.CONFIG_DEADLINE_TIME);
            if (redConfigRp != null && redConfigRp.getData() != null
                    && StringUtils.isNotBlank(redConfigRp.getData().getConfigValue())){
                deadlineMillisecond = new Integer(redConfigRp.getData().getConfigValue())*60*60*1000;
            }

            //根据orderNo查询红包信息
            RedOrder query = new RedOrder();
            query.setRedNo(inRedPayFallbackDto.getRequestNo());
            RedOrder redOrder = redOrderMapper.selectOne(new QueryWrapper<>(query));

            // 修改支付状态
            redOrder.setStatus("2");
            redOrder.setPayWay(inRedPayFallbackDto.getPayTool());
            redOrderMapper.updateById(redOrder);

            //新增发送记录
            RedImDistribute redImDistribute = new RedImDistribute();
            BeanUtils.copyProperties(redOrder,redImDistribute);
            Date date = new Date();
            redImDistribute.setSendTime(date);
            Date deadLineDate = new Date(date.getTime()+deadlineMillisecond);
            redImDistribute.setDeadlineTime(deadLineDate);
            redImDistributeMapper.insert(redImDistribute);

            //预生成红包金额
            List<String> moneyList = new ArrayList<>();
            if (DictConstant.RED_TYPE_LUCK.equals(redOrder.getRedType())){
                moneyList = RedUtil.getRandomRed(redOrder.getRedNum(),redOrder.getRedMoney(),null,null,String.class);
            }else if (DictConstant.RED_TYPE_NORMAL.equals(redOrder.getRedType())){
                for (int i=0;i<redOrder.getRedNum();i++){
                    moneyList.add(String.valueOf(redOrder.getRedPrice()));
                }
            }

            //红包金额放入缓存
            redisTemplate.opsForList().leftPushAll(RedConstants.HB_MONEY_LIST+redOrder.getRedNo(),moneyList);
            // 红包信息放入缓存
            Map<String,String> hbMap = new HashMap<>();
            // 红包个数
            hbMap.put(RedConstants.HB_SIZE,String.valueOf(redOrder.getRedNum()));
            // 红包过期时间
            hbMap.put(RedConstants.HB_DEADLINE,String.valueOf(System.currentTimeMillis()+deadlineMillisecond));
            redisTemplate.opsForHash().putAll(RedConstants.HB_INFO+redOrder.getRedNo(),hbMap);

            // TODO 发送延时队列 红包过期后 退还给发红包者
            sendMqForRedExpire(redOrder.getRedNo());
            // TODO 给智慧门户推送消息 发送红包
        }
    }

    private void sendMqForRedExpire(String redNo) {
        try {
            Message message = new Message(RocketMqConstants.RED_TOPIC, RocketMqConstants.TAGS_EXPIRE,
                    RocketMqConstants.TAGS_EXPIRE, redNo.getBytes());
            message.setDelayTimeLevel(RocketMqConstants.RED_EXPIRE_DELAY_LEVEL);
            SendResult sendResult = producer.send(message);
            if (!RocketMqConstants.SEND_OK.equals(sendResult.getSendStatus().toString())) {
                throw new BusinessException(sendResult.getSendStatus().toString());
            }
        }catch (Exception e){
            Map<String, Object> map = new HashMap<>();
            map.put("topic", RocketMqConstants.RED_TOPIC);
            map.put("tags", RocketMqConstants.TAGS_EXPIRE);
            map.put("data", redNo);
            redisTemplate.opsForList().leftPush(RocketMqConstants.ERROR_SEND, JSON.toJSONString(map));
            log.error("红包过期消息发送失败 红包编号 {},异常信息{}", redNo,e);
        }
    }
}
