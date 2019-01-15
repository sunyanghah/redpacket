package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.companyred.dto.InQueryActivityDto;
import com.tianrun.redpacket.companyred.dto.OutQueryActivityDto;
import com.tianrun.redpacket.companyred.entity.RedActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Mapper
public interface RedActivityMapper extends BaseMapper<RedActivity> {
    /**
     * 删除红包活动
     * @param inBatchIdDto
     */
    void deleteActivity(InBatchIdDto<Long> inBatchIdDto);

    /**
     * 分页查询红包活动
     * @param page
     * @param inQueryActivityDto
     * @return
     */
    List<OutQueryActivityDto> queryActivity(Page page, @Param("dto") InQueryActivityDto inQueryActivityDto);

    /**
     * 修改红包活动状态
     * @param inBatchIdDto
     * @param date
     * @param activityStatus
     * @param userAccount
     */
    void updateActivityStatus(@Param("ids") InBatchIdDto<Long> inBatchIdDto, @Param("updateTime") Date date,
                              @Param("activityStatus") String activityStatus,@Param("updateUser") String userAccount);
}
