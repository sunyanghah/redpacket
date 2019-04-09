package com.tianrun.redpacket.countred.service.impl;

import com.tianrun.redpacket.common.constant.RedConstants;
import com.tianrun.redpacket.common.dto.OutRedConfigDto;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.countred.dto.InUpdateConfigDto;
import com.tianrun.redpacket.countred.entity.RedConfig;
import com.tianrun.redpacket.countred.mapper.RedConfigMapper;
import com.tianrun.redpacket.countred.service.RedConfigService;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Service
public class RedConfigServiceImpl implements RedConfigService {

    @Autowired
    private RedConfigMapper redConfigMapper;

    @Override
    public void updateConfig(InUpdateConfigDto inUpdateConfigDto) throws Exception {
        checkConfig(inUpdateConfigDto);
        redConfigMapper.updateConfig(inUpdateConfigDto,new Date());
    }

    private void checkConfig(InUpdateConfigDto inUpdateConfigDto) throws Exception{
        String configValue = inUpdateConfigDto.getConfigValue();
        if (RedConstants.CONFIG_PERSONAL_MAX.equals(inUpdateConfigDto.getConfigCode())
                || RedConstants.CONFIG_GROUP_MAX.equals(inUpdateConfigDto.getConfigCode())){
            checkMoney(configValue);
        }else if (RedConstants.CONFIG_DEADLINE_TIME.equals(inUpdateConfigDto.getConfigCode())){
            try {
                Integer deadlineTime = new Integer(configValue);
                if (deadlineTime <= 0){
                    throw new BusinessException("退回时间必须大于0小时");
                }
            }catch (Exception e){
                throw new BusinessException("退回时间请输入正整数");
            }
        }
    }

    private void checkMoney(String configValue) throws Exception{
        try{
            Double money = new Double(configValue);
            if (money <= 0){
                throw new BusinessException("金额必须大于0");
            }
            String[] strs = configValue.split(".");
            if (strs.length == 2 && strs[1].length() > 2){
                throw new BuilderException("金额最多设置两位小数");
            }

        }catch (Exception e){
            throw new BusinessException("请输入正确的金额");
        }
    }

    @Override
    public List<OutRedConfigDto> getConfigList() throws Exception {
        List<RedConfig> redConfigList = redConfigMapper.selectList(null);
        if (redConfigList != null && redConfigList.size() > 0){
            List<OutRedConfigDto> outRedConfigDtoList = new ArrayList<>();
            OutRedConfigDto outRedConfigDto;
            for (RedConfig redConfig : redConfigList){
                outRedConfigDto = new OutRedConfigDto();
                BeanUtils.copyProperties(redConfig,outRedConfigDto);
                outRedConfigDtoList.add(outRedConfigDto);
            }
            return outRedConfigDtoList;
        }else{
            return null;
        }
    }

    @Override
    public OutRedConfigDto getConfigByCode(String configCode) throws Exception {
        return redConfigMapper.getConfigByCode(configCode);
    }
}
