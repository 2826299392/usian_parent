package com.usian.service;

import com.usian.pojo.TbContentCategory;
import java.util.List;


public interface ContentCategoryService {
    //查询分类内容信息
    List<TbContentCategory> selectContentCategoryByParentId(Long id);
}
