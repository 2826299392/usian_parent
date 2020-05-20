package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/content")
public class ContentCategoryController {

    //注入接口
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    //分类内容管理
    @RequestMapping("/selectContentCategoryByParentId")    //默认查询parendId为0的
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id){
        List<TbContentCategory> tbContentCategoryList=contentServiceFeign.selectContentCategoryByParentId(id);
        if(tbContentCategoryList.size()>0){
            return Result.ok(tbContentCategoryList);
        }
        return Result.error("查询失败");
    }
}
