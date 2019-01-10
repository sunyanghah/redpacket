package com.tianrun.redpacket.companyred.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.companyred.entity.RedAuthorization;
import com.tianrun.redpacket.companyred.mapper.RedAuthorizationMapper;
import com.tianrun.redpacket.companyred.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Service
public class AuthorizationServiceImpl extends ServiceImpl<RedAuthorizationMapper,RedAuthorization> implements AuthorizationService {

    @Autowired
    private RedAuthorizationMapper redAuthorizationMapper;

    @Override
    public List<RedAuthorization> getAuthListByRedId(Long id) throws Exception {
        return redAuthorizationMapper.getAuthListByRedId(id);
    }

    @Override
    public void deleteAuthOfRed(long redActivityId) throws Exception {
        redAuthorizationMapper.deleteAuthOfRed(redActivityId);
    }
}
