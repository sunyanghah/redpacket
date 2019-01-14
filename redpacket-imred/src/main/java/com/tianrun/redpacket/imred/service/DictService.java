package com.tianrun.redpacket.imred.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianrun.redpacket.common.dict.OutDictDto;
import com.tianrun.redpacket.common.dict.RedDict;

import java.util.List;

/**
 * Created by dell on 2019/1/11.
 * @author dell
 */
public interface DictService extends IService<RedDict>{

    /**
     * 根据字典type获取字典信息
     * @param type
     * @return
     * @throws Exception
     */
    List<OutDictDto> getDictByType(String type) throws Exception;
}
