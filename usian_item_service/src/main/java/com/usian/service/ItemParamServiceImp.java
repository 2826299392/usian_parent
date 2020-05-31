package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImp implements ItemParamService{

    //注入mapper
    @Autowired
    private TbItemParamMapper tbItemParamMapper;


    @Override
    public TbItemParam selectItemParamByItemCatId(Integer itemCatId) {
        TbItemParamExample example = new TbItemParamExample(); //创建逆向工程生成的sql工具类
        TbItemParamExample.Criteria criteria = example.createCriteria();  //创建相当于where条件
        criteria.andItemCatIdEqualTo(Long.valueOf(itemCatId));   //where条件后的参数信息
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExampleWithBLOBs(example);
        if(tbItemParamList!=null && tbItemParamList.size()>0){
            return tbItemParamList.get(0);
        }
        return null;
    }

    //分页查询商品规格参数信息
    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);   //从第几页查询，每页查询几条
        TbItemParamExample example = new TbItemParamExample();  //创建逆向工程生成的sql工具类
        example.setOrderByClause("updated DESC");    //排序条件
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExampleWithBLOBs(example);  //调用mapper查询

        PageInfo<TbItemParam> pageInfo = new PageInfo<>(tbItemParamList);  //将数据分装到pageinfo

        PageResult pageResult = new PageResult();  //创建对象
        pageResult.setPageIndex(pageInfo.getPageNum());  //分装每页条数
        pageResult.setTotalPage(pageInfo.getTotal());    //分装共多少页
        pageResult.setResult(pageInfo.getList());   //分装查询数据
        return pageResult;
    }

    //添加商品规格信息
    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {
        Date date = new Date();   //创建时间
        TbItemParamExample example = new TbItemParamExample();   //创建sql工具类
        TbItemParamExample.Criteria criteria = example.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);      //根据id查询
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExample(example);
        if(tbItemParamList.size()>0){   //判断如果查询有该规格信息，则返回0不添加
            return 0;
        }

        TbItemParam itemParam = new TbItemParam();   //创建对象
        itemParam.setItemCatId(itemCatId);
        itemParam.setParamData(paramData);    //补全空字段
        itemParam.setUpdated(date);
        itemParam.setCreated(date);
        int i = tbItemParamMapper.insertSelective(itemParam);
        return i;
    }

    @Override
    public Integer deleteItemParamById(Long id) {
        int i = tbItemParamMapper.deleteByPrimaryKey(id);  //删除
        return i;
    }
}
