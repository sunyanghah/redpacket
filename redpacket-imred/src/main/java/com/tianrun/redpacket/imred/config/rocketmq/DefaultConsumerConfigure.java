package com.tianrun.redpacket.imred.config.rocketmq;

import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by dell on 2019/1/4.
 */
@Configuration
@Log4j2
public abstract class DefaultConsumerConfigure {

    @Autowired
    private ConsumerConfig consumerConfig;

    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("framework-rocketmq");


    /**
     * 开启消费者监听服务
     * @throws MQClientException
     */
    public void listener() throws MQClientException {
        log.info("开启消费者-------------------");

        consumer.setNamesrvAddr(consumerConfig.getNamesrvAddr());

        consumer.subscribe("redTopic", "*");
        // 开启内部类实现监听
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> DefaultConsumerConfigure.this.dealBody(msgs));

        consumer.start();

        log.info("rocketmq启动成功---------------------------------------");

    }

    /**
     * 处理body的业务
     * @param msgs
     * @return
     */
    public abstract ConsumeConcurrentlyStatus dealBody(List<MessageExt> msgs);

}
