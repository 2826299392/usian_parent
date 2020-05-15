package com.usain.service;

import com.usian.pojo.TbItemParam;

public interface ItemParamService {
    //查询商品规格参数信息
    TbItemParam selectItemParamByItemCatId(Integer itemCatId);
}
