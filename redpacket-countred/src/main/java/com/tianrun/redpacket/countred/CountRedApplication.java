package com.tianrun.redpacket.countred;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@SpringBootApplication(scanBasePackages = {"com.tianrun.redpacket"})
@MapperScan(basePackages = {"com.tianrun.redpacket.countred.mapper","com.tianrun.redpacket.common.dict"})
public class CountRedApplication {
    public static void main(String[] args){
        SpringApplication.run(CountRedApplication.class,args);
    }
}
