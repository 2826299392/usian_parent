package com.usain.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.*;
import com.usian.pojo.*;

import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemServiceImp implements ItemService{

    //注入mapper商品
   @Autowired
   private TbItemMapper tbItemMapper;

   //注入描述表的mapper
    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    //注入规格表的mapper
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    //注入规格表的mapper
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

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
        example.setOrderByClause("updated desc");
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

    //添加商品基本信息，描述信息，规格信息
    @Override
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
        Long itemId = IDUtils. genItemId ();
        Date date = new Date();
        //1、补全商品表中的没有赋值的字段
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        Integer i1 = tbItemMapper.insertSelective(tbItem);

        //2、添加描述表中的描述, 补全空域字段
        TbItemDesc itemDesc=new TbItemDesc();
        itemDesc.setItemId(itemId);
        itemDesc.setCreated(date);
        itemDesc.setUpdated(date);
        itemDesc.setItemDesc(desc);
        Integer i2 = tbItemDescMapper.insertSelective(itemDesc);

        //3、添加规格信息，补全空余字段
        TbItemParamItem tbItemParamItem=new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setParamData(itemParams);
        Integer i3 = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        return i1+i2+i3;
    }

    @Override
    public Integer deleteItemById(Long itemId) {
        int i = tbItemMapper.deleteByPrimaryKey(itemId); //删除
        return i;
    }

    @Override
    public Map<String,Object> preUpdateItem(Long itemId) {
        Map<String,Object> map = new HashMap<String,Object>();

        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbItem.getCid());

        map.put("tbItem",tbItem);
        map.put("itemDesc",itemDesc);
        map.put("tbItemCat",tbItemCat);

        return map;
    }
}
