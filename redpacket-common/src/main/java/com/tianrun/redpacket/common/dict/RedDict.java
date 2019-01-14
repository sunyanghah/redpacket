package com.tianrun.redpacket.common.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Data
@TableName("tb_red_dict")
public class RedDict {

    @TableId(type = IdType.INPUT)
    private Integer id;

    /**
     * 字典type
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 字典code
     */
    @TableField("dict_code")
    private String dictCode;

    /**
     * 字典值
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}
