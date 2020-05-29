package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
@Service
@Transactional
public class ContentCategoryServiceImp implements ContentCategoryService{

    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        TbContentCategoryExample example = new TbContentCategoryExample();   //创建sql工具类
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);   //根据id查询
        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(example);
        return tbContentCategoryList;
    }

    //添加分类管理信息
    @Override
    public Integer insertContentCategory(TbContentCategory tbContentCategory) {
        //1、添加空余字段
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setStatus(1);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setUpdated(new Date());
        tbContentCategory.setCreated(new Date());
        Integer i = tbContentCategoryMapper.insertSelective(tbContentCategory);

        //2、查询添加后他的父节点数据
        TbContentCategory primaryKey = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());

        //3、判断父节点下的isparend是否有子节点，1\true为有，没有的改为有
        if (!primaryKey.getIsParent()){
            primaryKey.setIsParent(true);
            primaryKey.setUpdated(new Date());   //修改时间
            tbContentCategoryMapper.updateByPrimaryKey(primaryKey);
        }

        return i;
    }

    @Override
    public Integer deleteContentCategoryById(Long categoryId) {
        //1、根据id查询
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        if(tbContentCategory.getIsParent()==true){   //判断自己是不是父节点，如果是不容许删除
            return 0;
        }

        //2、不是父节点进行删除
        Integer i = tbContentCategoryMapper.deleteByPrimaryKey(categoryId);

        //3、根据父节点查询下面的所有子节点
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();  //创建sql语句的工具类
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();  //相当于where条件
        criteria.andParentIdEqualTo(tbContentCategory.getParentId());   //添加条件
        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);

        //4、判断以父节点是否查询到数据
        if(tbContentCategoryList.size()==0){   //说明没查到，将它改为不是父节点
            TbContentCategory tbContentCategory1 = new TbContentCategory();  //创建对象
            tbContentCategory1.setId(tbContentCategory.getParentId());    //复制id 根据id修改
            tbContentCategory1.setIsParent(false);
            tbContentCategory.setUpdated(new Date());   //修改时间
            tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory1);
        }
        return 200;
    }

    //修改
    @Override
    public Integer updateContentCategory(TbContentCategory tbContentCategory) {
      tbContentCategory.setUpdated(new Date());   //修改时间
        int i = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
        return i;
    }
}
