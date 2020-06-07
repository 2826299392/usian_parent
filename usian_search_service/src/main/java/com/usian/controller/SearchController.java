package com.usian.controller;

import com.usian.pojo.SearchItem;
import com.usian.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    //想索引库导入商品数据
    @RequestMapping("/importAll")
    public Boolean importAll(){
        return searchService.importAll();
    }

    //以关键字分页搜索查询商品信息
    @RequestMapping("/list")
    public List<SearchItem> selectByQ(String q, long page, Integer pageSize){
        return searchService.selectByQ(q,page,pageSize);
    }
}
