package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.utils.PageResult;

import java.util.Map;

public interface ItemService {
    //根据ID查询信息
    TbItem selectItemInfo(Long itemId);

    //查新商品信息分页处理
    PageResult selectTbItemAllByPage(Integer page, Integer rows);

    //添加商品信息
    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);

    //删除商品
    Integer deleteItemById(Long itemId);

    //查询要修改的商品信息
    Map<String,Object> preUpdateItem(Long itemId);

    //带参数修改商品
    Integer updateTbItem(TbItem item, String desc, String itemParams);

    //查询商品详情信息
    TbItemDesc selectItemDescByItemId(Long itemId);

    //根据商品订单号修改商品库存数量
    Integer updateTbItemByOrderId(String orderId);
}
