package com.tianrun.redpacket.companyred.web;

import com.tianrun.redpacket.common.platform.RP;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @PostMapping
    public RP addTask() throws Exception {

        return RP.buildSuccess("新增成功");
    }

}
