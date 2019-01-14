package com.tianrun.redpacket.imred.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.common.constant.RedConstants;
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
        redOrder.setId(idGenerator.next());
        redOrder.setRedNo(redOrderNo);
        redOrder.setDistributeTime(new Date());
        redOrder.setStatus("1");
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
            Date deadLineDate = new Date(date.getTime()+RedConstants.HB_DEADLINE_MILLISECOND);
            redImDistribute.setDeadlineTime(deadLineDate);
            redImDistributeMapper.insert(redImDistribute);

            // 红包信息放入缓存
            Map<String,String> hbMap = new HashMap<>();
            // 红包个数
            hbMap.put(RedConstants.HB_SIZE,String.valueOf(redOrder.getRedNum()));
            // 红包总钱数
            hbMap.put(RedConstants.HB_MONEY,String.valueOf(redOrder.getRedMoney()));
            // 红包类型
            hbMap.put(RedConstants.HB_TYPE,redOrder.getRedType());
            // 单个红包钱数。普通类型的红包时设置
            hbMap.put(RedConstants.HB_PRICE,null == redOrder.getRedPrice()?null:String.valueOf(redOrder.getRedPrice()));
            // 红包过期时间
            hbMap.put(RedConstants.HB_DEADLINE,String.valueOf(System.currentTimeMillis()+RedConstants.HB_DEADLINE_MILLISECOND));
            hbMap.put(RedConstants.HB_MAX_PRICE,"0");
            hbMap.put(RedConstants.HB_MIN_PRICE,"0");
            redisTemplate.opsForHash().putAll(RedConstants.HB_INFO+redOrder.getRedNo(),hbMap);

            // TODO 发送延时队列 红包过期后 退还给发红包者

            // TODO 给智慧门户推送消息 发送红包
        }
    }
}
