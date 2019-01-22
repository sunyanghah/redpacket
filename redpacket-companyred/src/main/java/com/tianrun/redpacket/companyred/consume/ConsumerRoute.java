package com.tianrun.redpacket.companyred.consume;

import com.alibaba.fastjson.JSON;
import com.tianrun.redpacket.common.constant.RocketMqConstants;
import com.tianrun.redpacket.companyred.config.rocketmq.DefaultConsumerConfigure;
import com.tianrun.redpacket.companyred.dto.UnpackMessageDto;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ConsumerRoute extends DefaultConsumerConfigure implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConsumeService consumeService;

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
                String topic = msg.getTopic();
                String tags = msg.getTags();
                String msgStr = new String(msg.getBody(), "utf-8");

                log.info("--topic--"+topic);
                log.info("--tags--"+tags);
                log.info("-----msg-----"+msgStr);

                if (RocketMqConstants.ACTIVITY_TOPIC.equals(topic) && RocketMqConstants.TAGS_UNPACK.equals(tags)){
                    UnpackMessageDto unpackMessageDto = JSON.parseObject(msgStr, UnpackMessageDto.class);
                    consumeService.unpackMessageHandle(unpackMessageDto);
                }
            } catch (Exception e) {
                log.error("消息处理失败");
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

