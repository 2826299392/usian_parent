package com.usian.conteoller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/detail")
public class DetailController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    //搜索到商品，点击商品根据ID查看商品信息
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(@RequestParam Long itemId){
        TbItem tbItem = itemServiceFeignClient.selectItemInfo(itemId);
        if(tbItem!=null){
            return Result.ok(tbItem);
        }
        return Result.error("查询搜索到的商品信息失败");
    }

    //根据Id查询商品详情数据信息
    @RequestMapping("/selectItemDescByItemId")
    public Result selectItemDescByItemId(@RequestParam Long itemId){
        TbItemDesc tbItemDesc = itemServiceFeignClient.selectItemDescByItemId(itemId);
        if (tbItemDesc!=null){
            return Result.ok(tbItemDesc);
        }
        return Result.error("查询商品详情失败");
    }

    //商品详情信息的规格参数信息
    @RequestMapping("selectTbItemParamItemByItemId")
    public Result selectTbItemParamItemByItemId(@RequestParam Long itemId){
        TbItemParamItem tbItemParamItem = itemServiceFeignClient.selectTbItemParamItemByItemId(itemId);
        if(tbItemParamItem!=null){
            return Result.ok(tbItemParamItem);
        }
        return Result.error("商品详情页的规格参数信息查询失败");
    }
}
