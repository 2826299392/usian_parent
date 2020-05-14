package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    //注入feign接口
    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem item=itemServiceFeignClient.selectItemInfo(itemId);
        if (item != null){
           return Result.ok(item);
        }
        return Result.error("查无结果");
    }
}
