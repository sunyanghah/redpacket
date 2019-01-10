package com.tianrun.redpacket.imred.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.imred.config.RedConstants;
import com.tianrun.redpacket.imred.dto.InRedPackDto;
import com.tianrun.redpacket.imred.dto.InRedPayFallbackDto;
import com.tianrun.redpacket.imred.dto.OutRedPackDto;
import com.tianrun.redpacket.imred.entity.RedImDistribute;
import com.tianrun.redpacket.imred.entity.RedOrder;
import com.tianrun.redpacket.imred.mapper.RedImDistributeMapper;
import com.tianrun.redpacket.imred.mapper.RedOrderMapper;
import com.tianrun.redpacket.imred.service.RedSendService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Service
public class RedSendServiceImpl implements RedSendService {

    @Autowired
    private RedOrderMapper redOrderMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedImDistributeMapper redImDistributeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutRedPackDto redPack(InRedPackDto inRedPackDto) throws Exception {
        OutRedPackDto outRedPackDto = new OutRedPackDto();
        // TODO 支付预下单

        RedOrder redOrder = new RedOrder();
        BeanUtils.copyProperties(inRedPackDto,redOrder);

        redOrder.setId(idGenerator.next());
        redOrder.setOrderNo(String.valueOf(idGenerator.next()));
        redOrder.setDistributeTime(new Date());
        redOrder.setStatus("1");
        redOrderMapper.insert(redOrder);

        // TODO 返回redirectUrl
        outRedPackDto.setPrePayId("this is redirectUrl ");
        return outRedPackDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payFallback(InRedPayFallbackDto inRedPayFallbackDto) throws Exception {
        if ("SUCCESS".equals(inRedPayFallbackDto.getStatus())){

            //根据orderNo查询红包信息
            RedOrder query = new RedOrder();
            query.setOrderNo(inRedPayFallbackDto.getRequestNo());
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
            Date deadLineDate = new Date(date.getTime()+RedConstants.HB_DEADLINE_MILLISECOND);
            redImDistribute.setDeadlineTime(deadLineDate);
            redImDistributeMapper.insert(redImDistribute);

            //红包信息放入缓存
            Map<String,String> hbMap = new HashMap<>();
            hbMap.put(RedConstants.HB_SIZE,String.valueOf(redOrder.getRedNum()));
            hbMap.put(RedConstants.HB_MONEY,String.valueOf(redOrder.getRedMoney()));
            // 红包类型
            hbMap.put(RedConstants.HB_TYPE,redOrder.getRedType());
            hbMap.put(RedConstants.HB_DEADLINE,String.valueOf(System.currentTimeMillis()+RedConstants.HB_DEADLINE_MILLISECOND));
            hbMap.put(RedConstants.HB_MAX_PRICE,"0");
            hbMap.put(RedConstants.HB_MIN_PRICE,"0");
            redisTemplate.opsForHash().putAll(RedConstants.HB_INFO+redOrder.getId(),hbMap);

            // TODO 发送延时队列 红包过期后 退还给发红包者

            //
        }
    }
}
