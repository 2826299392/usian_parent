package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/content")
public class ContentController {

    //注入接口
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    //内容管理分页查询
    @RequestMapping("/selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "30")Integer rows,
                                                 @RequestParam Long categoryId){
        PageResult pageResult = contentServiceFeign.selectTbContentAllByCategoryId(page,rows,categoryId);
        if(pageResult.getResult()!=null){
            return Result.ok(pageResult);
        }
        return Result.error("查询失败");
    }

    //内容管理添加
    @RequestMapping("/insertTbContent")
    public Result insertTbContent(TbContent tbContent){
        Integer num = contentServiceFeign.insertTbContent(tbContent);
        if(num > 0){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    //内容管理删除
    @RequestMapping("/deleteContentByIds")
    public Result deleteContentByIds(Long ids){
        Integer num = contentServiceFeign.deleteContentByIds(ids);
        if(num > 0){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
