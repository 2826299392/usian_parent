package com.usain.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class ItemCatServiceImp implements ItemCatService{

    //注入mapper
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    //查询商品类目
    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Integer parentId) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();   //创建逆向工程生成的sql工具类对象
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();  //创建类似于sql语句判断的where条件
        criteria.andParentIdEqualTo(Long.valueOf(parentId));   //where条件后的要求 parentID==？？
        criteria.andStatusEqualTo(1);               //where   状态为1的
        List<TbItemCat> tbItemCatList = tbItemCatMapper.selectByExample(tbItemCatExample);
        return tbItemCatList;
    }
}
