package com.usian.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-search-service")
public interface SearchFeign {

    //向索引库导入数据
    @RequestMapping("/service/search/importAll")
    Boolean importAll();
}
