package com.usain.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemMapper;
import com.usian.pojo.TbItem;

import com.usian.pojo.TbItemExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemServiceImp implements ItemService{

    //注入mapper
   @Autowired
   private TbItemMapper tbItemMapper;

   //根据ID查询商品信息
    @Override
    public TbItem selectItemInfo(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

    //查询商品信息进行分页处理查询
    @Override
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);     //参数1：查询第几页 参数二：每页显示几条数据

        TbItemExample example=new TbItemExample();    //创建逆向工程生成的实体类的sql工具类
        TbItemExample.Criteria criteria= example.createCriteria(); //创建类的条件 类似于where  sql语句判断让添加条件
        criteria.andStatusEqualTo((byte)1);  //创建条件是 and  status(字段)  =1 ：参数；

        List<TbItem> itemList = tbItemMapper.selectByExample(example);  //逆向工程生成的方法查询
        PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(itemList);     //分页

        PageResult result = new PageResult();         //创建接口文档返回的类
        result.setPageIndex(pageInfo.getPageNum());  //将pageinfo里的参数注入到返回的类中，当前页
        result.setTotalPage(pageInfo.getTotal());    //将pageinfo里的参数注入到返回的类中，总页数
        result.setResult(itemList);                  //将分页处理的数据分装到result
        return result;
    }
}
