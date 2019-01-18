package com.tianrun.redpacket.companyred.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * Created by dell on 2018/12/27.
 * @author dell
 */
@Component
public class RedisInitSerializer implements ApplicationListener {

    @Autowired
    private RedisTemplate redisTemplate;

    private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationReadyEvent){
            redisTemplate.setKeySerializer(stringRedisSerializer);
            redisTemplate.setDefaultSerializer(stringRedisSerializer);
            redisTemplate.setHashKeySerializer(stringRedisSerializer);
            redisTemplate.setHashValueSerializer(stringRedisSerializer);
            redisTemplate.setValueSerializer(stringRedisSerializer);
        }
    }
}
