package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    //注入feign接口
    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

   //根据ID查询单个商品信息
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem item=itemServiceFeignClient.selectItemInfo(itemId);
        if (item != null){
           return Result.ok(item);
        }
        return Result.error("查无结果");
    }

    //查询商品信息  分页查询处理
    @RequestMapping("/selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "2") Integer rows){
        PageResult pageResult=itemServiceFeignClient.selectTbItemAllByPage(page,rows);
        //对查询结果判断是否查到数据
        if(pageResult.getResult() != null && pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }
}
