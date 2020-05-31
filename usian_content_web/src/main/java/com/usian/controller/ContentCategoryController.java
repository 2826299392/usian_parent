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

    //内容分类管理添加  传递两个参数用对象接收
    @RequestMapping("/insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory){
       Integer num = contentServiceFeign.insertContentCategory(tbContentCategory);
       if(num > 0){
           return Result.ok();
       }
       return Result.error("添加失败");
    }

    //删除内容分类管理信息
    @RequestMapping("/deleteContentCategoryById")
    public Result deleteContentCategoryById(@RequestParam Long categoryId){
       Integer num = contentServiceFeign.deleteContentCategoryById(categoryId);
       if(num == 200){
          return Result.ok();
       }
       return Result.error("删除失败");
    }

    //修改分类管理信息
    @RequestMapping("/updateContentCategory")
    public Result updateContentCategory(TbContentCategory tbContentCategory){
       Integer num = contentServiceFeign.updateContentCategory(tbContentCategory);
       if(num>0){
           return Result.ok();
       }
       return Result.error("修改失败");
    }
}
