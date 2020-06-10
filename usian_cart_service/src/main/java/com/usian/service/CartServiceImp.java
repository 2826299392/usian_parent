package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class CartServiceImp implements CartService{

    @Value("${CART_REDIS_NAME}")
    private String CART_REDIS_NAME;

    @Autowired
    private RedisClient redisClient;

    //登录状态下，从redis中获取购物车商品信息
    @Override
    public Map<String, TbItem> getRedisUserIdCart(String userId) {
       return  (Map<String, TbItem>) redisClient.hget(CART_REDIS_NAME, userId);
    }

    @Override
    public Boolean addCartToRedis(String userId, Map<String, TbItem> cart) {
        return redisClient.hset(CART_REDIS_NAME,userId,cart);
    }
}
