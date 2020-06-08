package com.usian.controller;

import com.usian.pojo.TbItemParamItem;
import com.usian.service.ItemParamService;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/itemParam")
public class ItemParamController {

    //注入service
    @Autowired
    private ItemParamService itemParamService;

    //查询商品规格信息
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemParamByItemCatId(@PathVariable Integer itemCatId){
       return itemParamService.selectItemParamByItemCatId(itemCatId);
    }

    //分页查询商品规格参数信息
    @RequestMapping("/selectItemParamAll")
    public PageResult selectItemParamAll(Integer page,Integer rows){
        return itemParamService.selectItemParamAll(page,rows);
    }

    //添加商品规格信息
    @RequestMapping("/insertItemParam")
    public Integer insertItemParam(Long itemCatId ,String paramData){
        return itemParamService.insertItemParam(itemCatId,paramData);
    }

    //删除商品规格信息
    @RequestMapping("/deleteItemParamById")
    public Integer deleteItemParamById(Long id){
        return itemParamService.deleteItemParamById(id);
    }

    //商品详情页的规格参数查询
    @RequestMapping("/selectTbItemParamItemByItemId")
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId){
        return itemParamService.selectTbItemParamItemByItemId(itemId);
    }
}
