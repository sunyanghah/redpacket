package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@Data
@TableName("tb_red_task_result")
public class RedTaskResult {

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    /**
     * 任务id
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 用户账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 任务进行状态
     */
    @TableField("task_status")
    private String taskStatus;
}
