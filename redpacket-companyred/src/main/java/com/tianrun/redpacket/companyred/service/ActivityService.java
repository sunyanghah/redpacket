package com.tianrun.redpacket.companyred.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.companyred.dto.InAddActivityDto;
import com.tianrun.redpacket.companyred.dto.InQueryActivityDto;
import com.tianrun.redpacket.companyred.dto.OutGetActivityDto;
import com.tianrun.redpacket.companyred.dto.OutQueryActivityDto;
import com.tianrun.redpacket.companyred.entity.RedActivity;

import javax.validation.Valid;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
public interface ActivityService extends IService<RedActivity> {

    /**
     * 新增红包活动
     * @param inAddActivityDto
     * @throws Exception
     */
    void addActivity(InAddActivityDto inAddActivityDto) throws Exception;

    /**
     * 获取红包活动详细信息
     * @param id
     * @return
     * @throws Exception
     */
    OutGetActivityDto getActivity(Long id) throws Exception;

    /**
     * 删除红包活动
     * @param inBatchIdDto
     * @throws Exception
     */
    void deleteActivity(InBatchIdDto<Long> inBatchIdDto) throws Exception;

    /**
     * 分页查询红包活动
     * @param inQueryActivityDto
     * @return
     * @throws Exception
     */
    Page<OutQueryActivityDto> queryActivity(InQueryActivityDto inQueryActivityDto) throws Exception;

    /**
     * 修改红包活动
     * @param inAddActivityDto
     * @throws Exception
     */
    void updateActivity(InAddActivityDto inAddActivityDto) throws Exception;

    /**
     * 激活红包活动
     * @param inBatchIdDto
     * @throws Exception
     */
    void activeActivity(InBatchIdDto<Long> inBatchIdDto) throws Exception;

    /**
     * 冻结红包活动
     * @param inBatchIdDto
     * @throws Exception
     */
    void freezeActivity(InBatchIdDto<Long> inBatchIdDto) throws Exception;
}
