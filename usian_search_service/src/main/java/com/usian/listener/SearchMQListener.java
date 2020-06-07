package com.usian.listener;


import com.usian.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchMQListener {

    @Autowired   //注入实现索引的接口实现同步索引数据
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue",durable = "true"),  //消息对象，保持消息的持久化
            exchange = @Exchange(value = "item_exchage",type = ExchangeTypes.TOPIC),
            key = {"item.*"}

    ))
    public void listen(String msg) throws IOException {
        System.out.println("获取到的数据为："+msg);
        int result = searchService.addDocement(msg);
        if(result>0){
           throw new RuntimeException("同步失败");
        }
    }
}
