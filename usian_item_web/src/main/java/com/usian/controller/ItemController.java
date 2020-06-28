package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/backend/item")
@Api("商品管理接口")  //描述controller的作用
public class ItemController {

    //注入feign接口
    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

   //根据ID查询单个商品信息
    @PostMapping("/selectItemInfo")
    @ApiOperation(value = "查询商品基本信息", notes = "根据itemId查询该商品基本信息信息")//描述该方法的作用value ，notes是详细描述该方法信息
    @ApiImplicitParam(name = "itemId",type = "Long", value = "商品Id")//方法参数描述，单个参数的描述
    public Result selectItemInfo(Long itemId){
        TbItem item=itemServiceFeignClient.selectItemInfo(itemId);
        if (item != null){
           return Result.ok(item);
        }
        return Result.error("查无结果");
    }

    //查询商品信息  分页查询处理
    @GetMapping("/selectTbItemAllByPage")
    @ApiOperation(value = "分页查询商品信息",notes = "查询第几页，每页显示几条数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",type = "Integer",value = "查询第几页，页码"),
            @ApiImplicitParam(name = "rows",type = "Integer",value = "每页显示几条数据")
    })//方法参数描述，多个参数的描述
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
    @PostMapping("/insertTbItem")
    @ApiOperation(value = "商品添加",notes = "添加商品及描述规格参数信息")
    @ApiImplicitParams({
            //关于对象的在pojo里面指定
            @ApiImplicitParam(name = "desc",type = "String",value = "商品描述信息"),
            @ApiImplicitParam(name = "itemParams",type = "String",value = "商品规格参数信息")
    })
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

    //修改商品信息
    @RequestMapping("/updateTbItem")
    public Result updateTbItem(TbItem tbItem,String desc,String itemParams){
       Integer num = itemServiceFeignClient.updateTbItem(tbItem,desc,itemParams);
       if(num>0){
           return Result.ok();
       }
       return Result.error("修改商品信息失败");
    }
}
