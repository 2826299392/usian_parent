package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItemParam;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/itemParam")
public class ItemParamController {

    //注入接口
    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    //查询商品规格参数模板
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable Integer itemCatId){
        TbItemParam tbItemParam=itemServiceFeignClient.selectItemParamByItemCatId(itemCatId);
        if(tbItemParam!=null){
           return Result.ok(tbItemParam);
        }
        return Result.error("查无结果");
    }
}
