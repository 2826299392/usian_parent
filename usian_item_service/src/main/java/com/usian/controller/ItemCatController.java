package com.usian.controller;

import com.usian.service.ItemCatService;
import com.usian.pojo.TbItemCat;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/itemCategory")
public class ItemCatController {

    @Autowired
    private ItemCatService itemCatService;

    //查询商品类目
    @RequestMapping("/selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(Integer parentId){
        return itemCatService.selectItemCategoryByParentId(parentId);
    }

    //查询左侧商品分类类目信息
    @RequestMapping("/selectItemCategoryAll")
    public CatResult selectItemCategoryAll(){
        return itemCatService.selectItemCategoryAll();
    }
}
