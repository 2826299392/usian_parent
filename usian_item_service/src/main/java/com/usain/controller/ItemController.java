package com.usain.controller;

import com.usain.service.ItemService;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
