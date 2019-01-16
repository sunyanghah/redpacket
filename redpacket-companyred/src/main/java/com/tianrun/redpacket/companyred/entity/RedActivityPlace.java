package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2019/1/16.
 * @author dell
 */
@TableName("tb_red_activity_place")
@Data
public class RedActivityPlace {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    @TableField("activity_id")
    private Long activityId;

    @TableField("place_code")
    private String placeCode;

    @TableField("place_name")
    private String placeName;
}
