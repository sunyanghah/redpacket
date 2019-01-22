package com.tianrun.redpacket.countred.web;

import com.sun.deploy.util.Waiter;
import com.tianrun.redpacket.common.platform.RP;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dell on 2019/1/21.
 * @author dell
 */
@RestController
public class RedisTestController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    private static DefaultRedisScript redNormalScript = new DefaultRedisScript();
    static {
        redNormalScript.setResultType(String.class);
        redNormalScript.setScriptText("redis.call('hmset', '{hbInfo}1', 'key', '0121-1value') \n" );
//                "redis.call('hmset', '{hbInfo}2', 'key','0121-2value' )\n");
    }
    private static String noremalScript = "redis.call('hmset', '{hbInfo}1', 'key', '0121-1value')";


    @GetMapping("/test")
    public RP test() throws Exception{
//        Object object = lettuceConnectionFactory.getConnection().eval(noremalScript.getBytes(), ReturnType.VALUE,0);
        redisTemplate.opsForHash().put("{hbInfo}3","key","value3");
        Object object = redisTemplate.execute(redNormalScript,
                null);
        return RP.buildSuccess(object);
    }

}
