package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.companyred.dto.ActivityPlaceDto;
import com.tianrun.redpacket.companyred.entity.RedActivityPlace;
import com.tianrun.redpacket.companyred.mapper.RedActivityPlaceMapper;
import com.tianrun.redpacket.companyred.service.ActivityPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dell on 2019/1/16.
 * @author dell
 */
@Service
public class ActivityPlaceServiceImpl extends ServiceImpl<RedActivityPlaceMapper,RedActivityPlace>
        implements ActivityPlaceService {

    @Autowired
    private RedActivityPlaceMapper redActivityPlaceMapper;

    @Override
    public List<ActivityPlaceDto> getPlaceListByRedId(Long activityId) throws Exception {
        return redActivityPlaceMapper.getPlaceListByRedId(activityId);
    }

    @Override
    public void deletePlaceOfRed(long redActivityId) throws Exception {
        redActivityPlaceMapper.deletePlaceOfRed(redActivityId);
    }
}
