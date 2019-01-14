package com.tianrun.redpacket.imred.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianrun.redpacket.common.dict.OutDictDto;
import com.tianrun.redpacket.common.dict.RedDict;
import com.tianrun.redpacket.common.dict.RedDictMapper;
import com.tianrun.redpacket.imred.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dell on 2019/1/11.
 * @author dell
 */
@Service
public class DictServiceImpl extends ServiceImpl<RedDictMapper,RedDict> implements DictService {

    @Autowired
    private RedDictMapper redDictMapper;

    @Override
    public List<OutDictDto> getDictByType(String type) throws Exception {
        return redDictMapper.getDictByType(type);
    }
}
