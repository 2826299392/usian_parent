package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    //登录状态下，从redis中获取购物车商品信息
    @RequestMapping("/getRedisUserIdCart")
    public Map<String, TbItem> getRedisUserIdCart(@RequestParam String userId){
        return cartService.getRedisUserIdCart(userId);
    }

    //添加购物车到redis中
    @RequestMapping("/addCartToRedis")
    public Boolean addCartToRedis(@RequestParam String userId,@RequestBody Map<String,TbItem> cart){
        return cartService.addCartToRedis(userId,cart);
    }
}
