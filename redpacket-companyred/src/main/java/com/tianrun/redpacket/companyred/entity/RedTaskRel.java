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
@TableName("tb_red_task_rel")
public class RedTaskRel {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    @TableField("red_id")
    private Long redId;

    @TableField("task_id")
    private Long taskId;
}
