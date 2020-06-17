package com.usian.controller;

import com.usian.CartServiceFeign;
import com.usian.feign.OrderFeign;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderShipping;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frontend/order")
public class OrderController {

    @Autowired   //注入购物车接口
    private CartServiceFeign cartServiceFeign;

    @Autowired  //注入订单接口
    private OrderFeign orderFeign;

    //查看商品订单页面信息
    @RequestMapping("/goSettlement")
    public Result goSettlement(String userId,String[] ids){
        //获取reids购物车
        Map<String, TbItem> cart = cartServiceFeign.getRedisUserIdCart(userId);
        //前台获取的是list集合的数据创建空的list集合
        List<TbItem> list = new ArrayList<>();
        //遍历前台勾选结算的商品id
        for ( String itemId : ids) {
            TbItem tbItem = cart.get(itemId);  //根据itemId获取cart购物车中商品信息
            list.add(tbItem);
        }
        if(list.size()>0){
            return Result.ok(list);
        }
        return Result.error("结算错误");
    }

    //提交订单商品的信息，将提交的订单商品信息保存到数据库
    @RequestMapping("/insertOrder")
    public Result insertOrder(String orderItem, TbOrderShipping tbOrderShipping, TbOrder tbOrder){
         //每次请求只有一个响应，创建一个大的pojo装在参数
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderItem(orderItem);
        orderInfo.setTbOrder(tbOrder);
        orderInfo.setTbOrderShipping(tbOrderShipping);
        String orderId = orderFeign.insertOrder(orderInfo);   //添加成功后返回一个订单号，提供给用户
        if(orderId!=null){
            return Result.ok(orderId);
        }
        return Result.error("订单错误");
    }
}
