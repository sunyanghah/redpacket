package com.tianrun.redpacket.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by dell on 2019/4/12.
 * @author dell
 */
@SpringBootApplication(scanBasePackages = {"com.tianrun.redpacket"})
@MapperScan(basePackages = {"com.tianrun.redpacket.gateway.mapper","com.tianrun.redpacket.common.dict"})
@EnableZuulProxy
@EnableOAuth2Sso
@EnableSwagger2
public class RedGatewayApplication {
    public static void main(String[] args){
        SpringApplication.run(RedGatewayApplication.class,args);
    }
}
