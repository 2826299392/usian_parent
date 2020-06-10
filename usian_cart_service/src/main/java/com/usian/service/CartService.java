package com.usian.service;

import com.usian.pojo.TbItem;

import java.util.Map;

public interface CartService {
    //登录状态下，从redis中获取购物车商品信息
    Map<String, TbItem> getRedisUserIdCart(String userId);

    //添加购物车到redis中
    Boolean addCartToRedis(String userId, Map<String, TbItem> cart);
}
