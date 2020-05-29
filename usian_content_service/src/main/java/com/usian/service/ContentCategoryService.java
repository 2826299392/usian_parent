package com.usian.service;

import com.usian.pojo.TbContentCategory;
import java.util.List;


public interface ContentCategoryService {
    //查询分类内容信息
    List<TbContentCategory> selectContentCategoryByParentId(Long id);

    //内容分类管理添加
    Integer insertContentCategory(TbContentCategory tbContentCategory);

    //删除
    Integer deleteContentCategoryById(Long categoryId);

    //修改信息
    Integer updateContentCategory(TbContentCategory tbContentCategory);
}
