package com.usian.service;

import com.usian.pojo.TbContent;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;

import java.util.List;

public interface ContentService {
    //分页查询内容信息
    PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId);

    //内容管理添加
    Integer insertTbContent(TbContent tbContent);

    //内容管理删除
    Integer deleteContentByIds(Long ids);

    //大广告查询
    List<AdNode> selectFrontendContentByAD();
}
