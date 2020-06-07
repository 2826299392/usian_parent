package com.usian.feign;

import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-search-service")
public interface SearchFeign {

    //向索引库导入数据
    @RequestMapping("/service/search/importAll")
    Boolean importAll();

    //以关键字分页搜索商品信息
    @RequestMapping("/service/search/list")
    List<SearchItem> selectByQ(@RequestParam String q,@RequestParam long page,@RequestParam Integer pageSize);
}
