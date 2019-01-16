package com.tianrun.redpacket.companyred.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.companyred.dto.ActivityPlaceDto;
import com.tianrun.redpacket.companyred.entity.RedActivityPlace;

import java.util.List;

/**
 * Created by dell on 2019/1/16.
 * @author dell
 */
public interface ActivityPlaceService extends IService<RedActivityPlace> {
    /**
     * 根据红包id获取场景列表
     * @param activityId
     * @return
     * @throws Exception
     */
    List<ActivityPlaceDto> getPlaceListByRedId(Long activityId) throws Exception;

    /**
     * 根据红包id删除场景列表
     * @param redActivityId
     * @throws Exception
     */
    void deletePlaceOfRed(long redActivityId) throws Exception;
}
