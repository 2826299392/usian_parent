package com.usain.service;

import com.usian.pojo.TbItemCat;

import java.util.List;

public interface ItemCatService {
    //查询商品类目
    List<TbItemCat> selectItemCategoryByParentId(Integer parentId);
}
