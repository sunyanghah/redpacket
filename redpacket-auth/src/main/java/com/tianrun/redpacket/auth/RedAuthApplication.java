package com.tianrun.redpacket.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dell on 2019/4/12.
 * @author dell
 */
@SpringBootApplication(scanBasePackages = {"com.tianrun.redpacket"})
@MapperScan(basePackages = {"com.tianrun.redpacket.auth.mapper","com.tianrun.redpacket.common.dict"})
public class RedAuthApplication {
    public static void main(String[] args){
        SpringApplication.run(RedAuthApplication.class,args);
    }
}
