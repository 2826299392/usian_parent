package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.utils.CatResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/frontend/itemCategory")
public class ItemCategoryController {

    //注入接口
  @Autowired
  private ItemServiceFeignClient itemServiceFeignClient;

    //查询左侧商品分类类目信息
    @RequestMapping("/selectItemCategoryAll")
    public Result selectItemCategoryAll(){
       CatResult catResult =itemServiceFeignClient.selectItemCategoryAll();
       if(catResult!=null){
           return Result.ok(catResult);
       }
        return Result.error("左侧分类信息===查询失败");
    }
}
