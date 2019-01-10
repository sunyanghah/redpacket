package com.tianrun.redpacket.companyred.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianrun.redpacket.common.dto.InBatchIdDto;
import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.companyred.dto.InAddActivityDto;
import com.tianrun.redpacket.companyred.dto.InAddTaskDto;
import com.tianrun.redpacket.companyred.dto.InQueryActivityDto;
import com.tianrun.redpacket.companyred.dto.OutGetActivityDto;
import com.tianrun.redpacket.companyred.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 新增企业红包活动
     * @param inAddActivityDto
     * @return
     * @throws Exception
     */
    @PostMapping
    public RP addActivity(@RequestBody @Valid InAddActivityDto inAddActivityDto) throws Exception {
        activityService.addActivity(inAddActivityDto);
        return RP.buildSuccess("新增成功");
    }

    /**
     * 获取企业红包活动详细信息
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public RP getActivity(@PathVariable("id")Long id) throws Exception{
        OutGetActivityDto outGetActivityDto = activityService.getActivity(id);
        return RP.buildSuccess(outGetActivityDto);
    }


    /**
     * 修改红包活动
     * @param inAddActivityDto
     * @return
     * @throws Exception
     */
    @PutMapping
    public RP updateActivity(@RequestBody @Valid InAddActivityDto inAddActivityDto) throws Exception{
        activityService.updateActivity(inAddActivityDto);
        return RP.buildSuccess("修改成功");
    }

    /**
     * 删除红包活动
     * @param inBatchIdDto
     * @return
     * @throws Exception
     */
    @DeleteMapping
    public RP deleteActivity(@RequestBody @Valid InBatchIdDto<Long> inBatchIdDto) throws Exception{
        activityService.deleteActivity(inBatchIdDto);
        return RP.buildSuccess("删除成功");
    }

    /**
     * 分页查询红包活动
     * @param inQueryActivityDto
     * @return
     * @throws Exception
     */
    @PostMapping("/listPage")
    public RP queryActivity(@RequestBody @Valid InQueryActivityDto inQueryActivityDto) throws Exception{
        Page page = activityService.queryActivity(inQueryActivityDto);
        return RP.buildSuccess(page);
    }

}
