package com.usian.controller;

import com.usian.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
