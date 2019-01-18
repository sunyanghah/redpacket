package com.tianrun.redpacket.companyred.web;

import com.tianrun.redpacket.common.platform.IdGenerator;
import com.tianrun.redpacket.common.platform.RP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dell on 2019/1/17.
 * @author dell
 */
@RestController
public class IdTestController {

    Set<Long> set = new HashSet<>();

    @Autowired
    private IdGenerator idGenerator;

    @GetMapping("/idTest")
    public RP test() throws Exception{
        set.add(idGenerator.next());
        return RP.buildSuccess("");
    }

    @GetMapping("/getSize")
    public RP getSize() throws Exception{
        return RP.buildSuccess(set.size());
    }

    @GetMapping("/clear")
    public RP clear() throws Exception{
        set.clear();
        return RP.buildSuccess("");
    }

    @GetMapping("/loopTest")
    public RP loopTest() throws Exception{
        Set set2 = new HashSet<>();
        for (int i =0;i<1000;i++){
            set2.add(idGenerator.next());
        }
        return RP.buildSuccess(set2.size());
    }
}
