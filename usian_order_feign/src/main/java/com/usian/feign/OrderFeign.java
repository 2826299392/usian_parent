package com.usian.feign;

import com.usian.pojo.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-order-service")
public interface OrderFeign {

    //提交订单商品信息返回订单好响应该用户
    @RequestMapping("/service/order/insertOrder")
    String insertOrder(@RequestBody OrderInfo orderInfo);
}
