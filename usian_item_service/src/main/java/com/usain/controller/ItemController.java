package com.usain.controller;

import com.usain.service.ItemService;
import com.usian.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/item")
public class ItemController {

    //注入service业务层
    @Autowired
    private ItemService itemService;

    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return itemService.selectItemInfo(itemId);
    }

}
