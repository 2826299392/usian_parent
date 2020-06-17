package com.usian.service;

import com.usian.pojo.OrderInfo;

public interface OrderService {
    //提交订单信息保存到数据库返回订单号
    String insertOrder(OrderInfo orderInfo);
}
