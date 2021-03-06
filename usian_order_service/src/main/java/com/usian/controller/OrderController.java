package com.usian.controller;

import com.usian.pojo.OrderInfo;
import com.usian.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //提交订单商品信息保存到数据库返回订单号
    @RequestMapping("/insertOrder")
    public String insertOrder(@RequestBody OrderInfo orderInfo){
        return orderService.insertOrder(orderInfo);
    }
}
