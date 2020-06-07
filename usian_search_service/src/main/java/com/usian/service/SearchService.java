package com.usian.service;

import com.usian.pojo.SearchItem;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    //导入商品信息到索引库
    Boolean importAll();

    //以关键字分页搜索商品信息
    List<SearchItem> selectByQ(String q, long page, Integer pageSize);

    //添加商品的时候，根据Mq得到的消息商品的ID，实现同步到索引库中
    int addDocement(String msg) throws IOException;
}
