package com.usain.service;

import com.usain.pojo.TbItemParam;
import com.usain.utils.PageResult;

public interface ItemParamService {
    //查询商品规格信息
    TbItemParam selectItemParamByItemCatId(Integer itemCatId);

    //分页查询商品规格参数信息
    PageResult selectItemParamAll(Integer page, Integer rows);

    //添加商品规格信息
    Integer insertItemParam(Long itemCatId, String paramData);

    //删除商品规格信息
    Integer deleteItemParamById(Long id);
}
