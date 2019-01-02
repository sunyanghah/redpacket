package com.tianrun.redpacket.common.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dell on 2018/8/20.
 * @author dell
 */
@Data
public class BaseEntity<T extends BaseEntity> extends Model<T> {

    /***
     * 创建人
     */
    @TableField("create_user")
    private Integer createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 最后一次修改人
     */
    @TableField("update_user")
    private Integer updateUser;

    /**
     * 最后一次修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 删除标记 0：未删除 1：已删除
     */
    @TableField("del_flag")
    private String delFlag;

    @Override
    protected Serializable pkVal() {
        return null;
    }

    public void preUpdate(Integer updateUser)  throws Exception{
        this.updateTime = new Date();
        this.updateUser = updateUser;
    }
    public void preInsert(Integer createUser) throws Exception{
        Date date = new Date();
        this.updateTime = date;
        this.createTime = date;
        this.createUser = createUser;
        this.updateUser = createUser;
        this.delFlag = "0";
    }
    public void preDelete(Integer deleteUser) throws Exception{
        this.updateUser = deleteUser;
        this.delFlag = "1";
        this.updateTime = new Date();
    }
}
