package com.usain.controller;

import com.usain.service.ItemCatService;
import com.usian.pojo.TbItemCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/itemCategory")
public class ItemCatController {

    @Autowired
    private ItemCatService itemCatService;

    //查询商品类目
    @RequestMapping("/selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(Integer parentId){
        return itemCatService.selectItemCategoryByParentId(parentId);
    }
}