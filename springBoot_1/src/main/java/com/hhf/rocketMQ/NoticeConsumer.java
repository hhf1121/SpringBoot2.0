package com.hhf.rocketMQ;

import com.alibaba.fastjson.JSONArray;
import com.hhf.vo.NotificationUserMQVo;
import com.hhf.webSocket.MsgWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;


@Slf4j
@Component
@Order(3)
public class NoticeConsumer implements CommandLineRunner {


    @Autowired
    private MsgWebSocketServer webSocketServer;

    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    DefaultMQPushConsumer consumer=new DefaultMQPushConsumer("noticeGroup");

    /**
     * 初始化RocketMq的监听信息，渠道信息
     */
    public void messageListener(){

        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setMessageModel(MessageModel.BROADCASTING);//广播模式
        try {

            //订阅某Topic下所有类型的消息，Tag用符号 * 表示:consumer.subscribe("MQ_TOPIC", "*", new MessageListener() {……});
            //订阅某Topic下某一种类型的消息，请明确标明Tag：consumer.subscribe("MQ_TOPIC", "TagA", new MessageListener() {……});
            //订阅某Topic下多种类型的消息，请在多个Tag之间用 || 分隔:consumer.subscribe("MQ_TOPIC", "TagA||TagB", new MessageListener() {……});
            // 订阅PushTopic下Tag为push的消息,都订阅消息
            consumer.subscribe("noticeTopic", "noticeTag");
            // 程序第一次启动从消息队列头获取数据
//            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //可以修改每次消费消息的数量，默认设置是每次消费一条
//            consumer.setConsumeMessageBatchMaxSize(1);
            //在此监听中消费信息，并返回消费的状态信息
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                // 会把不同的消息分别放置到不同的队列中
                for(Message msg:msgs){
                    try {
                        String info = new String(msg.getBody(), "utf-8");
                        log.info(getClass().getName()+"接收到了消息："+info);
                        NotificationUserMQVo vo = JSONArray.parseObject(info, NotificationUserMQVo.class);
                        List<String> userIds = vo.getUserIds();
                        if(!userIds.isEmpty()){//发给所有在线用户
                            for (String userId : userIds) {
                                webSocketServer.sendOneMessage(userId,vo.getType());
                            }
                        }else {
                            webSocketServer.sendAllMessage(vo.getMsg());
                        }
                        log.info("消息发送到websocket成功...");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
            log.info("noticeConsumer,启动成功...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListener(){
        consumer.shutdown();
        log.info("registerConsumer,停止成功...");
    }

    @Override
    public void run(String... args) throws Exception {
        this.messageListener();
    }

}
