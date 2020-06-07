package com.usian.controller;

import com.usian.feign.SearchFeign;
import com.usian.pojo.SearchItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchController {

    @Autowired
    private SearchFeign searchFeign;

    //想索引库导入商品信息数据
    @RequestMapping("/importAll")
    public Result importAll(){
       Boolean s = searchFeign.importAll();
       if(s){
          return Result.ok();
       }
        return Result.error("导入失败");
    }

    //以关键字进行分页搜索商品信息
    @RequestMapping("list")
    public List<SearchItem> selectByQ(String q, @RequestParam(defaultValue = "1") long page,
                                                @RequestParam(defaultValue = "20") Integer pageSize){
        List<SearchItem> searchItemList = searchFeign.selectByQ(q, page, pageSize);
        for (SearchItem searchItem : searchItemList) {
            System.out.println(searchItem);
        }
        return searchItemList;

    }
}
