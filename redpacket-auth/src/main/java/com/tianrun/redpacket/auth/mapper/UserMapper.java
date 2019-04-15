package com.tianrun.redpacket.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.auth.entity.SysRole;
import com.tianrun.redpacket.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * Created by dell on 2018/10/31.
 * @author dell
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户账号获取用户信息
     * @param username
     * @return
     */
    @Select("        SELECT\n" +
            "        *\n" +
            "        FROM\n" +
            "        sys_user\n" +
            "        WHERE\n" +
            "        username = #{username}")
    SysUser getUserByUsername(@Param("username") String username);

    /**
     * 根据用户id获取角色信息
     * @param userId
     * @return
     */
    @Select("        SELECT\n" +
            "        sr.*\n" +
            "        FROM\n" +
            "        sys_user_role sur\n" +
            "        INNER JOIN\n" +
            "        sys_role sr\n" +
            "        ON\n" +
            "        sr.id = sur.role_id\n" +
            "        WHERE\n" +
            "        sur.user_id = #{userId}")
    List<SysRole> getRolesByUserId(@Param("userId") Integer userId);
}
