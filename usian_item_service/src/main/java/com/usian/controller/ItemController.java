package com.usian.controller;

import com.usian.pojo.TbItemDesc;
import com.usian.service.ItemService;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/item")
public class ItemController {

    //注入service业务层
    @Autowired
    private ItemService itemService;

    //根据ID查询商品信息
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return itemService.selectItemInfo(itemId);
    }

    //查询商品信息分页处理查询
    @RequestMapping("/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(Integer page,Integer rows){
        return itemService.selectTbItemAllByPage(page,rows);
    }

    //添加商品基本信息，描述信息，规格信息，三表
    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem, @RequestParam String desc, @RequestParam String itemParams){
        return itemService.insertTbItem(tbItem,desc,itemParams);
    }

    //删除
    @RequestMapping("/deleteItemById")
    public Integer deleteItemById(@RequestParam Long itemId){
        return itemService.deleteItemById(itemId);
    }

    //查询商品修改信息
    @RequestMapping("/preUpdateItem")
    public Map<String,Object> preUpdateItem(@RequestParam Long itemId){
       return itemService.preUpdateItem(itemId);
    }

    //带参数修改商品信息
    @RequestMapping("/updateTbItem")
    public Integer updateTbItem(@RequestBody TbItem item,@RequestParam String desc,@RequestParam String itemParams){
        return itemService.updateTbItem(item,desc,itemParams);
    }

    //根据Id查询商品详情数据信息
    @RequestMapping("/selectItemDescByItemId")
    public TbItemDesc selectItemDescByItemId(Long itemId){
        return itemService.selectItemDescByItemId(itemId);
    }

}
