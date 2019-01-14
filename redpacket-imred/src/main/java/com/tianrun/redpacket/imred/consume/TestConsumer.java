package com.tianrun.redpacket.imred.consume;

import com.alibaba.fastjson.JSON;
import com.tianrun.redpacket.imred.config.rocketmq.DefaultConsumerConfigure;
import com.tianrun.redpacket.imred.entity.RedGrab;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by dell on 2019/1/4.
 * @author dell
 */
@Log4j2
@Configuration
public class TestConsumer extends DefaultConsumerConfigure implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        try {
            super.listener();
        } catch (MQClientException e) {
            log.error("消费者监听器启动失败", e);
        }

    }

    @Override
    public ConsumeConcurrentlyStatus dealBody(List<MessageExt> msgs)  {
        for(MessageExt msg : msgs) {
            try {
                String msgStr = new String(msg.getBody(), "utf-8");
//                JSON.parseObject(msgStr, RedGrab.class);
                log.info("--topic--"+msg.getTopic());
                log.info("--tags--"+msg.getTags());
                log.info("----------------"+msgStr);
            } catch (Exception e) {
                log.error("消息处理失败");
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

