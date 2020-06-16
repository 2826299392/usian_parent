package com.usian.controller;

import com.usian.CartServiceFeign;
import com.usian.pojo.TbItem;
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

    @Autowired
    private CartServiceFeign cartServiceFeign;

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
}
