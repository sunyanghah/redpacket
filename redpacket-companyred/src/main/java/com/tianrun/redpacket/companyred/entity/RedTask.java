package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 */
@TableName("tb_red_task")
@Data
public class RedTask {

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    /**
     * 任务编码
     */
    @TableField("task_code")
    private String taskCode;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务描述
     */
    @TableField("task_desc")
    private String taskDesc;

    /**
     * 任务开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 任务到期时间
     */
    @TableField("deadline_time")
    private Date deadlineTime;

    /**
     * 安全key
     */
    @TableField("secure_key")
    private String secureKey;

    /**
     * 完成编码
     */
    @TableField("finished_code")
    private String finishedCode;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 是否可访问
     */
    @TableField("access_flag")
    private String accessFlag;

    /**
     * 是否可领取
     */
    @TableField("useful_flag")
    private String usefulFlag;
}
