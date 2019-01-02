package com.tianrun.redpacket.imred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dell on 2018/12/22.
 * @author dell
 */
@SpringBootApplication(scanBasePackages = {"com.tianrun.redpacket"})
public class ImRedApplication {
    public static void main(String[] args){
        SpringApplication.run(ImRedApplication.class,args);
    }
}
