package com.usian.quartz;

import com.usian.mq.MQSender;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.TbOrder;
import com.usian.redis.RedisClient;
import com.usian.service.LocalMessageService;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private MQSender mqSender;


    @Autowired
    private LocalMessageService localMessageService;
    //要做的任务：关闭超时订单任务
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if(redisClient.setnx("SETNX_ORDER_LOCK_KEY",ip,30)){
            System.out.println("======执行关闭超时订单任务======"+new Date());
            //1、查询我们的商品订单中所有超时的订单商品
            List<TbOrder> tbOrderList = orderService.selectOverTimeTbOrder();

            //2、遍历超时订单商品，将超时的订单商品的状态设置为关闭状态
            for (TbOrder tbOrder : tbOrderList) {
                //将订单商品的订单ID传到实现层修改状态
                orderService.updateOverTimeTbOrder(tbOrder.getOrderId());

                //3、完成订单商品超时的数量加入到商品库存中加入回去
                orderService.updateTbItemByOrderId(tbOrder.getOrderId());
            }

            System.out.println("执行扫描本地消息表的任务...." + new Date());
            List<LocalMessage> localMessages = localMessageService.selectLocalMessageByStatus(0);  //查询状态为0的消息发送失败的消息
            for (LocalMessage localMessage : localMessages) {      //遍历发送失败的消息
                mqSender.sendMsg(localMessage);    //将消息记录表中失败的消息，在次发送
            }
            redisClient.del("SETNX_ORDER_LOCK_KEY");
        }else {
            System.out.println("============机器："+ip+" 占用分布式锁，任务正在执行=============="+new Date());
        }


    }
}
