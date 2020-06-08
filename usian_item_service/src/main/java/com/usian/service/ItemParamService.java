package com.usian.service;

import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.PageResult;

public interface ItemParamService {
    //查询商品规格信息
    TbItemParam selectItemParamByItemCatId(Integer itemCatId);

    //分页查询商品规格参数信息
    PageResult selectItemParamAll(Integer page, Integer rows);

    //添加商品规格信息
    Integer insertItemParam(Long itemCatId, String paramData);

    //删除商品规格信息
    Integer deleteItemParamById(Long id);

    //商品详情页的规格参数信息查询
    TbItemParamItem selectTbItemParamItemByItemId(Long itemId);
}
