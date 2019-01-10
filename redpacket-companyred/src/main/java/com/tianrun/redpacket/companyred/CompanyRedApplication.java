package com.tianrun.redpacket.companyred;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@SpringBootApplication(scanBasePackages = {"com.tianrun.redpacket"})
@MapperScan(basePackages = {"com.tianrun.redpacket.companyred.mapper","com.tianrun.redpacket.common.mapper"})
public class CompanyRedApplication {
    public static void main(String[] args){
        SpringApplication.run(CompanyRedApplication.class,args);
    }
}
