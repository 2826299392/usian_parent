package com.usain.service;

import com.usain.pojo.TbItemCat;
import com.usain.utils.CatResult;

import java.util.List;

public interface ItemCatService {
    //查询商品类目
    List<TbItemCat> selectItemCategoryByParentId(Integer parentId);

    //查询左侧商品分类类目信息
    CatResult selectItemCategoryAll();
}
