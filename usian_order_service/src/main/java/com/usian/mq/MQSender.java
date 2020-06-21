package com.usian.mq;


import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务：
 *      1、发送消息
 *      2、消息确认成功后修改local_message表中消息的状态 改为1，消息发送成功，下次不再扫描发送这条消息
 */
@Component
public class MQSender implements ReturnCallback , ConfirmCallback {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    //作用： 消息发送失败时调用
    @Override           //参数一是回退的消息内容   4：是那个交换器   5：是那个routingKey的消息
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("return message:回退的消息是"+message.getBody().toString()+"这个交换器下的："+s1+"这个routingKey下的"+s2);
    }



    public void sendMsg(LocalMessage localMessage) {
        RabbitTemplate rabbitTemplate = (RabbitTemplate) this.amqpTemplate;//将amqpTemplate接口转换为rabbitListener子类调用方法

        rabbitTemplate.setMandatory(true);  //开启回退，消息没发送成功回退给发送者
        rabbitTemplate.setReturnCallback(this);   //消息发送失败调用
        rabbitTemplate.setConfirmCallback(this);  //消息确认成功返回

        //用于确认之后更改本地消息状态或删除本地消息--本地消息id
        CorrelationData correlationData = new CorrelationData(localMessage.getTxNo());                         //消息记录的消息Id
        rabbitTemplate.convertAndSend("order_exchange","order.deleteNum",JsonUtils.objectToJson(localMessage),correlationData);
    }

    //作用：下游服务：  消息确认成功返回后调用
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        String id = correlationData.getId();   //回去消息Id
        if(ack){   //判断消息是否被确认
            // 消息发送成功,更新本地消息为已成功发送状态或者直接删除该本地消息记录
            LocalMessage localMessage = new LocalMessage();
            localMessage.setTxNo(id);       //这个消息
            localMessage.setState(1);       //修改消息状态为 1 发送成功不再发送
            localMessageMapper.updateByPrimaryKeySelective(localMessage);
        }
    }

}
