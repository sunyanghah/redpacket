package com.tianrun.redpacket.countred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.common.dto.OutRedConfigDto;
import com.tianrun.redpacket.countred.dto.InUpdateConfigDto;
import com.tianrun.redpacket.countred.entity.RedConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Mapper
public interface RedConfigMapper extends BaseMapper<RedConfig>{

    /**
     * 根据配置code获取配置值
     * @param configCode
     * @return
     */
    @Select("SELECT\n" +
            "id,\n" +
            "config_code as configCode,\n" +
            "config_value as configValue,\n" +
            "config_desc as configDesc,\n" +
            "remarks,\n" +
            "update_user as updateUser,\n" +
            "update_time as updateTime\n" +
            "FROM\n" +
            "tb_red_config\n" +
            "WHERE\n" +
            "config_code = #{configCode}")
    OutRedConfigDto getConfigByCode(@Param("configCode") String configCode);

    /**
     * 根据配置code修改配置信息
     * @param inUpdateConfigDto
     * @param updateTime
     */
    @Update(" <script> " +
            " update " +
            " tb_red_config " +
            " set config_value = #{dto.configValue} " +
            " , update_user = #{dto.updateUser} " +
            " , update_time = #{updateTime} " +
            " where " +
            " config_code = #{dto.configCode}" +
            " </script> ")
    void updateConfig(@Param("dto") InUpdateConfigDto inUpdateConfigDto, @Param("updateTime")Date updateTime);
}
