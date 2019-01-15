package com.tianrun.redpacket.imred.config.rocketmq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dell on 2019/1/4.
 * @author dell
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rocketmq.consume")
@Configuration
@ToString
public class ConsumerConfig {
    private String groupName;

    private String namesrvAddr;
}
