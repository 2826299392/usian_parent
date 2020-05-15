package com.usain.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;

public interface ItemService {
    //根据ID查询信息
    TbItem selectItemInfo(Long itemId);

    //查新商品信息分页处理
    PageResult selectTbItemAllByPage(Integer page, Integer rows);
}
