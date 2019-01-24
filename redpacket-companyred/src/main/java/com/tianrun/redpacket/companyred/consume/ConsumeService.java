package com.tianrun.redpacket.companyred.consume;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianrun.redpacket.common.constant.DictConstant;
import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.companyred.dto.UnpackMessageDto;
import com.tianrun.redpacket.companyred.entity.RedActivity;
import com.tianrun.redpacket.companyred.entity.RedGrab;
import com.tianrun.redpacket.companyred.mapper.RedActivityMapper;
import com.tianrun.redpacket.companyred.mapper.RedGrabMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dell on 2019/1/14.
 * @author dell
 */
@Component
@Slf4j
public class ConsumeService {

    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private RedGrabMapper redGrabMapper;
    @Autowired
    private RedActivityMapper redActivityMapper;

    /**
     * 拆红包消息消费
     * @param unpackMessageDto
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void unpackMessageHandle(UnpackMessageDto unpackMessageDto) throws Exception {
        addGrabNote(unpackMessageDto);
        if (unpackMessageDto.isLastOne()){
            RedActivity redActivity = new RedActivity();
            redActivity.setRedNo(unpackMessageDto.getRedNo());
            redActivity = redActivityMapper.selectOne(new QueryWrapper<>(redActivity));
            if (redActivity != null && DictConstant.RED_TYPE_LUCK.equals(redActivity.getRedType())) {
                setBestLuck(unpackMessageDto.getRedNo());
            }
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
