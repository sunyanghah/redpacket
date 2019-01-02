package com.tianrun.redpacket.imred.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Data
@TableName("tb_red_grab")
public class RedGrab {

    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 红包id
     */
    @TableField("red_id")
    private Long redId;

    /**
     * 用户账号
     */
    @TableField("user_account")
    private String user_account;

    /**
     * 抢到的金额
     */
    @TableField("money")
    private Integer money;

    /**
     * 抢到的时间
     */
    @TableField("grab_time")
    private Date grabTime;

    /**
     * 是否是运气王 0否 1是
     */
    @TableField("best_luck")
    private String bestLuck;

    /**
     * 红包来源(企业红包或IM红包)
     */
    @TableField("red_source")
    private String redSource;
}
