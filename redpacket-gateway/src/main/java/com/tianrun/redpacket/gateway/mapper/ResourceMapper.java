package com.tianrun.redpacket.gateway.mapper;

import com.tianrun.redpacket.gateway.dto.OutResourcesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dell on 2018/11/26.
 * @author dell
 */
@Mapper
public interface ResourceMapper {

    /**
     * 根据角色集合查询可访问的资源列表
     * @param roles
     * @return
     */
    @Select("<script>" +
            " SELECT\n" +
            "        sre.url as resourceUrl,\n" +
            "        sre.method as resourceMethod,\n" +
            "        sre.service_id as serviceId\n" +
            "        FROM\n" +
            "        sys_role sr\n" +
            "        INNER JOIN\n" +
            "        sys_role_resources srr\n" +
            "        ON\n" +
            "        sr.id = srr.role_id\n" +
            "        INNER JOIN\n" +
            "        sys_resources sre\n" +
            "        ON\n" +
            "        sre.id = srr.resources_id\n" +
            "        WHERE\n" +
            "        sr.name in (\n" +
            "        <foreach collection=\"list\" item=\"roleCode\" separator=\",\">\n" +
            "            #{roleCode}\n" +
            "        </foreach>\n" +
            "        )" +
            "</script>")
    List<OutResourcesDto> getResourcesByRoles(@Param("list") List<String> roles);
}
