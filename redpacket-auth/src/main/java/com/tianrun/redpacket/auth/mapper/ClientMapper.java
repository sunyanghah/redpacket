package com.tianrun.redpacket.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.auth.entity.SysOauthClientDetails;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by dell on 2018/11/12.
 * @author dell
 */
@Mapper
public interface ClientMapper extends BaseMapper<SysOauthClientDetails> {
}
