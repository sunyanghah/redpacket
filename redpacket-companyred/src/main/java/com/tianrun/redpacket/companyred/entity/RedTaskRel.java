package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
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
