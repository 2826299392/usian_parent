package com.usian.listener;

import com.rabbitmq.client.Channel;
import com.usian.pojo.DeDuplication;
import com.usian.pojo.LocalMessage;
import com.usian.service.DeDuplicationService;
import com.usian.service.ItemService;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ItemMQListener {

    @Autowired
    private ItemService itemService;

    @Autowired
    private DeDuplicationService deDuplicationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "item_queue",durable = "true"),
            exchange = @Exchange(value = "order_exchange",type = ExchangeTypes.TOPIC),  //在该交换机获取，消息类型
            key = {"*.*"}    //根据routing获取指定消息
    ))
    public void listener(String orderId, Channel channel, Message message) throws IOException {
        System.out.println("获取到的消息是"+orderId);

        LocalMessage localMessage = JsonUtils.jsonToPojo(orderId, LocalMessage.class);  //将发送过来的消息进行转换
        DeDuplication deDuplications = deDuplicationService.selectDeDuplicationByTxNo(localMessage.getTxNo());  //查询去重表中这个消息
        if(deDuplications==null){   //如果这个消息为空说明消费成功
            Integer result = itemService.updateTbItemByOrderId(localMessage.getOrderNo());  //根据商品订单号修改商品库存数量
            if (!(result>0)){
                throw  new RuntimeException("扣减商品库存失败");
            }
            deDuplicationService.insertDeDuplication(localMessage.getTxNo());   //记录消费成功的消息
        }else {
            System.out.println("=======幂等生效：事务"+deDuplications.getTxNo()+" 已成功执行===========");
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);  //手动ack  每次处理一条
    }
}
