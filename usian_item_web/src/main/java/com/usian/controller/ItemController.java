package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    //注入feign接口
    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

   //根据ID查询单个商品信息
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem item=itemServiceFeignClient.selectItemInfo(itemId);
        if (item != null){
           return Result.ok(item);
        }
        return Result.error("查无结果");
    }

    //查询商品信息  分页查询处理
    @RequestMapping("/selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "2") Integer rows){
        PageResult pageResult=itemServiceFeignClient.selectTbItemAllByPage(page,rows);
        //对查询结果判断是否查到数据
        if(pageResult.getResult() != null && pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }

    //添加商品信息，参数1为商品基本信息。参数二商品描述信息，参数三，商品规格信息，三张表
    @RequestMapping("/insertTbItem")
    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer Num = itemServiceFeignClient.insertTbItem(tbItem,desc,itemParams);
        if(Num==3){
            return Result.ok();
        }
     return Result.error("添加错误");
    }

    //删除商品
    @RequestMapping("/deleteItemById")
    public Result deleteItemById(Long itemId){
        Integer integer = itemServiceFeignClient.deleteItemById(itemId);
        if(integer!=null){
            return Result.ok();
        }
        return Result.error("删除错误");
    }

    //查询更新商品的信息
    @RequestMapping("/preUpdateItem")
    public Result preUpdateItem(Long itemId){
        Map<String,Object> map = itemServiceFeignClient.preUpdateItem(itemId);

        if(map!=null){
            return Result.ok(map);
        }
        return Result.error("查询失败");
    }
}
