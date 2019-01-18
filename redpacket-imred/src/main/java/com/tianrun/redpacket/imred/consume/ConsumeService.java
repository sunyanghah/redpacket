package com.tianrun.redpacket.imred.consume;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.imred.dto.UnpackMessageDto;
import com.tianrun.redpacket.imred.entity.RedGrab;
import com.tianrun.redpacket.imred.entity.RedImDistribute;
import com.tianrun.redpacket.imred.entity.RedRefund;
import com.tianrun.redpacket.imred.mapper.RedGrabMapper;
import com.tianrun.redpacket.imred.mapper.RedImDistributeMapper;
import com.tianrun.redpacket.imred.mapper.RedRefundMapper;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 拆红包消息消费
     * @param unpackMessageDto
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void unpackMessageHandle(UnpackMessageDto unpackMessageDto) throws Exception {
        addGrabNote(unpackMessageDto);
        if (unpackMessageDto.isLastOne()){
            setBestLuck(unpackMessageDto.getRedNo());
        }
    }

    /**
     * 过期红包消息消费
     * @param redNo
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void expireMessageHandle(String redNo) throws Exception {

        List<String> list = redisTemplate.opsForHash().multiGet(RedConstants.HB_INFO + redNo,
                Arrays.asList(RedConstants.HB_DEADLINE, RedConstants.HB_SIZE, RedConstants.HB_MONEY));

        if (null != list && list.size() > 0 && !list.contains(null)) {
            int size = Integer.parseInt(list.get(1));
            int money = Integer.parseInt(list.get(2));

            if (size > 0 && money > 0) {

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
                redRefund.setRefundMoney(money);
                redRefund.setRefundReason("红包超时未领退回");
                redRefund.setRefundTime(new Date());
                redRefund.setRemarks(remarks.toString());
                redRefundMapper.insert(redRefund);


                //TODO 退回红包发送人的余额

                //TODO 删除redis中的数据 hbInfo 和 hbUser
            }
        }


        System.out.println(redNo+"过期了-------------------------------------"+System.currentTimeMillis());
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

    public void showTest(String msgStr) {
        log.info("this is server2 and "+msgStr);
    }
}
