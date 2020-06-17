package com.usian.service;

import com.usian.mapper.TbOrderItemMapper;
import com.usian.mapper.TbOrderMapper;
import com.usian.mapper.TbOrderShippingMapper;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderItem;
import com.usian.pojo.TbOrderShipping;
import com.usian.redis.RedisClient;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImp implements OrderService{

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Long ORDER_ID_BEGIN;  //定义订单号最小数

    @Value("${ORDER_ITEM_ID_KEY}")
    private String ORDER_ITEM_ID_KEY;

    @Autowired   //注入redis工具
    private RedisClient redisClient;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public String insertOrder(OrderInfo orderInfo) {
        //1、解析orderInfo
        TbOrder tbOrder = orderInfo.getTbOrder();  //获取订单基本信息信息
        TbOrderShipping tbOrderShipping = orderInfo.getTbOrderShipping();  //获取订单物流信息
        List<TbOrderItem> tbOrderItems = JsonUtils.jsonToList(orderInfo.getOrderItem(), TbOrderItem.class);  //获取订单商品详细信息

        //2、保存订单基本信息
        if(!redisClient.exists("ORDER_ID_KEY")){  //判断是否存在该订单号
            //设置订单号的初始值
           redisClient.set(ORDER_ID_KEY,ORDER_ID_BEGIN);
        }
        Long orderId = redisClient.incr(ORDER_ID_KEY, 1L);  //调用自增一获取性的订单号
        tbOrder.setOrderId(orderId.toString());
        tbOrder.setCreateTime(new Date());   //补齐空余字段
        tbOrder.setUpdateTime(new Date());
        //1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        tbOrder.setStatus(1);
        tbOrderMapper.insertSelective(tbOrder);

        //3、保存订单商品详细信息
        if(!redisClient.exists(ORDER_ITEM_ID_KEY)){  //判断是否存在该明细Id
            redisClient.set(ORDER_ITEM_ID_KEY,0);
        }
        for (TbOrderItem tbOrderItem : tbOrderItems) {   //遍历商品详细信息的集合
               //生成明细Id
               Long orderItemId = redisClient.incr(ORDER_ITEM_ID_KEY, 1L);
               tbOrderItem.setId(orderItemId.toString());
               tbOrderItem.setOrderId(orderId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }

        //4、保存订单物流信息
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShippingMapper.insertSelective(tbOrderShipping);

        //通过MQ发送消息在提交订单购买成功后，扣除item中商品的库存数量
        amqpTemplate.convertAndSend("order_exchange","order.deleteNum",orderId);

        return orderId.toString();
    }
}
