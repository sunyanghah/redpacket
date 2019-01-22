package com.tianrun.redpacket.companyred.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Data
@TableName("tb_red_grab")
public class RedGrab {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 红包编号
     */
    @TableField("red_no")
    private String redNo;

    /**
     * 用户账号
     */
    @TableField("user_account")
    private String userAccount;

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
