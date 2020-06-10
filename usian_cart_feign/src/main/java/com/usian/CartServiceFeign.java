package com.usian;

import com.usian.pojo.TbItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-cart-service")
public interface CartServiceFeign {

    //登录状态下，从redis中获取购物车商品信息
    @RequestMapping("/service/cart/getRedisUserIdCart")
    Map<String, TbItem> getRedisUserIdCart(@RequestParam String userId);

    //3、将我们的购物车保存到redis中
    @RequestMapping("/service/cart/addCartToRedis")
    Boolean addCartToRedis(@RequestParam String userId, Map<String, TbItem> cart);
}
