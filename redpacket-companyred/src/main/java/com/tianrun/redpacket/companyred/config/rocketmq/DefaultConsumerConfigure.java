package com.tianrun.redpacket.companyred.config.rocketmq;

import com.tianrun.redpacket.common.constant.RocketMqConstants;
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
 * @author dell
 */
@Configuration
@Log4j2
public abstract class DefaultConsumerConfigure {

    @Autowired
    private ConsumerConfig consumerConfig;


    /**
     * 开启消费者监听服务
     *
     * */
    public void listener() throws MQClientException {
        log.info("开启消费者-------------------");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerConfig.getGroupName());
        consumer.setNamesrvAddr(consumerConfig.getNamesrvAddr());

        consumer.subscribe(RocketMqConstants.ACTIVITY_TOPIC, "*");
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
