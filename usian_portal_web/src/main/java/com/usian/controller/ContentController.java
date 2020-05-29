package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.utils.AdNode;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/content")
public class ContentController {
    //注入接口
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    //查询前台大广告
    @RequestMapping("/selectFrontendContentByAD")
    public Result selectFrontendContentByAD(){
        List<AdNode> adNode = contentServiceFeign.selectFrontendContentByAD();
        if(adNode!=null){
            return Result.ok(adNode);
        }
        return Result.error("查询大广告失败");
    }
}
