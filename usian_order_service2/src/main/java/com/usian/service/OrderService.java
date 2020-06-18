package com.usian.service;

import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;

import java.util.List;

public interface OrderService {
    //提交订单信息保存到数据库返回订单号
    String insertOrder(OrderInfo orderInfo);

    //1、查询我们的商品订单中所有超时的订单商品
    List<TbOrder> selectOverTimeTbOrder();

    //将订单商品的订单ID传到实现层修改状态
    void updateOverTimeTbOrder(String orderId);

    //3、完成订单商品超时的数量加入到商品库存中加入回去
    void updateTbItemByOrderId(String orderId);
}
