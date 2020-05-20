package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //分页查询商品规格参数信息
    @RequestMapping("/selectItemParamAll")
    public Result selectItemParamAll(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "3") Integer rows){
        PageResult pageResult = itemServiceFeignClient.selectItemParamAll(page,rows);
        //判断是否查询到数据
        if(pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
     return Result.error("查询失败");
    }

    //添加商品规格模板信息
    @RequestMapping("/insertItemParam")
    public Result insertItemParam(@RequestParam Long itemCatId,String paramData){
        Integer num = itemServiceFeignClient.insertItemParam(itemCatId,paramData);
        if(num != 0){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    //删除商品规格信息
    @RequestMapping("/deleteItemParamById")
    public Result deleteItemParamById(@RequestParam Long id){
        Integer num = itemServiceFeignClient.deleteItemParamById(id);
        if(num != null){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
