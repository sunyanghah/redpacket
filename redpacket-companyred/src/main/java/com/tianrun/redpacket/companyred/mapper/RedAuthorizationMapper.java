package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.companyred.entity.RedAuthorization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Mapper
public interface RedAuthorizationMapper extends BaseMapper<RedAuthorization> {

    /**
     * 根据红包id查询领取人员范围
     * @param id
     * @return
     */
    List<RedAuthorization> getAuthListByRedId(Long id);

    /**
     * 根据红包id删除领取人员范围信息
     * @param redActivityId
     */
    void deleteAuthOfRed(long redActivityId);
}
