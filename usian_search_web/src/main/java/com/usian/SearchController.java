package com.usian;

import com.usian.feign.SearchFeign;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
