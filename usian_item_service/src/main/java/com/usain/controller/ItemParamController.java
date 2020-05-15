package com.usain.controller;

import com.usain.service.ItemParamService;
import com.usian.pojo.TbItemParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/itemParam")
public class ItemParamController {

    //注入service
    @Autowired
    private ItemParamService itemParamService;

    //查询商品规格参数信息
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemParamByItemCatId(@PathVariable Integer itemCatId){
       return itemParamService.selectItemParamByItemCatId(itemCatId);
    }
}
