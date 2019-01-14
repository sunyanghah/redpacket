package com.tianrun.redpacket.imred.web;

import com.tianrun.redpacket.common.dict.OutDictDto;
import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.imred.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dell on 2019/1/11.
 * @author dell
 */
@RestController
@RequestMapping("/dict")
public class RedDictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/type/{type}")
    public RP getDictByType(@PathVariable("type")String type) throws Exception {
        List<OutDictDto> dictDtoList = dictService.getDictByType(type);
        return RP.buildSuccess(dictDtoList);
    }

}
