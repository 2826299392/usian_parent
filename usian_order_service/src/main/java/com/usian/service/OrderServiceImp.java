package com.usian.service;

import com.usian.mapper.*;
import com.usian.mq.MQSender;
import com.usian.pojo.*;
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

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired   //注入记录本地消息表的mapper
    private LocalMessageMapper localMessageMapper;

    @Autowired   //注入发消息的类
    private MQSender mqSender;

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


        //记录本地消息表    将订单订单号，和订单的状态记录到本地
        LocalMessage localMessage = new LocalMessage();  //创建本地记录消息的对象
        localMessage.setOrderNo(orderId.toString());     //要记录的消息
        localMessage.setTxNo(UUID.randomUUID().toString());   //设置一下这个消息的消息Id
        localMessage.setState(0);   //设置一下这个消息的状态  0 就是默认是这个消息发送失败没成功，在扫描的时候重新发送这条消息
        localMessageMapper.insertSelective(localMessage);   //将消息记录到数据库

        //发送消息，修改本地消息表的状态       消息要发送orderId   还要发送消息自身Id，所以发送对象
        mqSender.sendMsg(localMessage);

        return orderId.toString();
    }

    //1、查询我们的商品订单中所有超时的订单商品
    @Override
    public List<TbOrder> selectOverTimeTbOrder() {
        return tbOrderMapper.selectOverTimeTbOrder();
    }

    //将订单商品的订单ID传到实现层修改状态
    @Override
    public void updateOverTimeTbOrder(String orderId) {
        TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(orderId);
        tbOrder.setStatus(6);
        tbOrder.setEndTime(new Date());
        tbOrder.setCloseTime(new Date());
        tbOrder.setUpdateTime(new Date());
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }

    //3、完成订单商品超时的数量加入到商品库存中加入回去
    @Override
    public void updateTbItemByOrderId(String orderId) {
        //1、根据订单ID查询该订单下的所有商品详细信息
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(tbOrderItemExample);
        
        //2、遍历订单商品
        for (TbOrderItem tbOrderItem : tbOrderItems) {
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum()+tbOrderItem.getNum());
            tbItem.setUpdated(new Date());
            tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }

    }
}
