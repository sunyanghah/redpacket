package com.tianrun.redpacket.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.common.entity.RedDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@Mapper
public interface RedDictMapper extends BaseMapper<RedDict> {

    /**
     * 根据字典type和code获取字典值
     * @param type
     * @param code
     * @return
     */
    @Select("select dict_value from tb_red_dict where dict_type = #{type} and dict_code = #{code}")
    String getValueByTypeAndCode(@Param("type") String type,@Param("code") String code);

    @Select("<script> " +
            "select dict_code as dictCode,dict_value as dictValue from tb_red_dict where dict_type = #{type} " +
            "and dict_code in (<foreach collection=\"codes\" item=\"code\" separator=\",\">" +
            "#{code}</foreach>)" +
            "</script>")
    List<Map<String,String>> getValueByTypeAndCodes(@Param("type")String type,@Param("codes")List<String> codes);

    @Select("<script> " +
            "select dict_type as dictType,dict_code as dictCode,dict_value as dictValue " +
            "from tb_red_dict where dict_type in( " +
            "<foreach collection=\"types\" item=\"type\" separator=\",\"> " +
            "#{type}</foreach>) " +
            "and dict_code in (<foreach collection=\"codes\" item=\"code\" separator=\",\"> " +
            "#{code}</foreach>) " +
            "</script> ")
    List<Map<String,String>> getValueByTypesAndCodes(@Param("types")List<String> types,@Param("codes")List<String> codes);
}
