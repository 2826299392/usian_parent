package com.usian.listener;

import com.usian.service.ItemService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemMQListener {

    @Autowired
    private ItemService itemService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "item_queue",durable = "true"),
            exchange = @Exchange(value = "order_exchange",type = ExchangeTypes.TOPIC),  //在该交换机获取，消息类型
            key = {"*.*"}    //根据routing获取指定消息
    ))
    public void listener(String orderId){
        System.out.println("获取到的消息是"+orderId);
        Integer result = itemService.updateTbItemByOrderId(orderId);  //根据商品订单号修改商品库存数量
        if (!(result>0)){
            throw  new RuntimeException("扣减商品库存失败");
        }
    }
}
