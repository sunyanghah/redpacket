package com.tianrun.redpacket.companyred.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.companyred.dto.*;
import com.tianrun.redpacket.companyred.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 新增任务
     * @param inAddTaskDto
     * @return
     * @throws Exception
     */
    @PostMapping
    public RP addTask(@RequestBody @Valid InAddTaskDto inAddTaskDto) throws Exception {
        taskService.addTask(inAddTaskDto);
        return RP.buildSuccess("新增成功");
    }

    /**
     * 获取任务详细信息
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public RP<OutGetTaskDto> getTask(@PathVariable("id")Long id) throws Exception{
        OutGetTaskDto outGetTaskDto = taskService.getTask(id);
        return RP.buildSuccess(outGetTaskDto);
    }

    /**
     * 修改任务
     * @param inUpdateTaskDto
     * @return
     * @throws Exception
     */
    @PutMapping
    public RP updateTask(@Valid @RequestBody InUpdateTaskDto inUpdateTaskDto) throws Exception{
        taskService.updateTask(inUpdateTaskDto);
        return RP.buildSuccess("修改成功");
    }

    /**
     * 删除任务
     * @param inBatchIdDto
     * @return
     * @throws Exception
     */
    @DeleteMapping
    public RP deleteTask(@RequestBody @Valid InBatchIdDto<Long> inBatchIdDto) throws Exception{
        taskService.deleteTask(inBatchIdDto);
        return RP.buildSuccess("删除成功");
    }

    /**
     * 分页查询任务
     * @param inQueryTaskDto
     * @return
     * @throws Exception
     */
    @PostMapping("/listPage")
    public RP queryTask(@Valid @RequestBody InQueryTaskDto inQueryTaskDto) throws Exception{
        Page page = taskService.queryTask(inQueryTaskDto);
        return RP.buildSuccess(page);
    }

    @PutMapping("/status")
    public RP changeStatus(@Valid @RequestBody InChangeTaskStatusDto inChangeTaskStatusDto) throws Exception{

        return RP.buildSuccess("修改成功");
    }

}
