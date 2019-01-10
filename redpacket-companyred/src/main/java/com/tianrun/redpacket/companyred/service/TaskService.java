package com.tianrun.redpacket.companyred.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.companyred.dto.InAddTaskDto;
import com.tianrun.redpacket.companyred.dto.InQueryTaskDto;
import com.tianrun.redpacket.companyred.dto.InUpdateTaskDto;
import com.tianrun.redpacket.companyred.dto.OutGetTaskDto;
import com.tianrun.redpacket.companyred.entity.RedTask;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
public interface TaskService extends IService<RedTask> {

    /**
     * 新增任务
     * @param inAddTaskDto
     * @throws Exception
     */
    void addTask(InAddTaskDto inAddTaskDto) throws Exception;

    /**
     * 根据id获取任务详细信息
     * @param id
     * @return
     * @throws Exception
     */
    OutGetTaskDto getTask(Long id) throws Exception;

    /**
     * 分页查询
     * @param inQueryTaskDto
     * @return
     * @throws Exception
     */
    Page queryTask(InQueryTaskDto inQueryTaskDto) throws Exception;

    /**
     * 修改任务
     * @param inUpdateTaskDto
     * @throws Exception
     */
    void updateTask(InUpdateTaskDto inUpdateTaskDto) throws Exception;

    /**
     * 删除任务
     * @param inBatchIdDto
     * @throws Exception
     */
    void deleteTask(InBatchIdDto<Long> inBatchIdDto) throws Exception;
}
