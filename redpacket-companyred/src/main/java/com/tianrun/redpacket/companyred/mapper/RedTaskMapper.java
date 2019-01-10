package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.companyred.dto.InQueryTaskDto;
import com.tianrun.redpacket.companyred.dto.OutQueryTaskDto;
import com.tianrun.redpacket.companyred.entity.RedTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Mapper
public interface RedTaskMapper extends BaseMapper<RedTask> {

    /**
     * 分页查询
     * @param page
     * @param inQueryTaskDto
     * @return
     */
    List<OutQueryTaskDto> queryTask(Page page, @Param("dto") InQueryTaskDto inQueryTaskDto);

    /**
     * 批量删除
     * @param inBatchIdDto
     */
    void deleteTask(InBatchIdDto<Long> inBatchIdDto);
}
