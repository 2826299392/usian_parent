package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.redis.RedisClient;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class ItemCatServiceImp implements ItemCatService{

    //注入mapper
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    //注入redis缓存库
    @Autowired
    private RedisClient redisClient;

    //获取到首页商品分类的key值
    @Value("${PROTAL_CATRESULT_KEY}")
    private String PROTAL_CATRESULT_KEY;

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

    //查询商品左侧分类类目信息，需要递归查询，递归单独方法调用
    @Override
    public CatResult selectItemCategoryAll() {
        //1、先从redis缓存中查询
        CatResult catResultRedis = (CatResult) redisClient.get(PROTAL_CATRESULT_KEY);
        if(catResultRedis!=null){  //如果不为空直接返回
            return catResultRedis;
        }
        //2、redis没查到，从数据库中查询
        List<?> catlist = getCatlist(0L);//默认查询父节点为0的
        CatResult catResult = new CatResult();   //创建对象，将数据放到对象中的data集合变量中
        catResult.setData(catlist);

        //3、存数据仓库中查到添加到redis缓存中
        redisClient.set(PROTAL_CATRESULT_KEY,catResult);
        return catResult;
    }
    //递归方法查询信息
    public List<?> getCatlist(Long parentId){
        TbItemCatExample tbItemCatExample = new TbItemCatExample();   //创建sql语句工具类
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();  //创建where添加
        criteria.andParentIdEqualTo(parentId);      //添加条件要求
        List<TbItemCat> tbItemCatList = tbItemCatMapper.selectByExample(tbItemCatExample);  //查询

        List arrayList = new ArrayList<>();    //创建集合将数据放到集合中
        int count=0;      //定义变量，前台只需要展示18条数据

        for (int i = 0; i < tbItemCatList.size(); i++) {       //for循环遍历查到的集合数据
            TbItemCat tbItemCat =  tbItemCatList.get(i);      //获取每条数据的对象
            if(tbItemCat.getIsParent()){                    //判断这条数据是否是父节点 如果是父节点，存放他的名称，在根据它的id查询下面的子节点
                CatNode catNode = new CatNode();           //创建存放数据的对象
                catNode.setName(tbItemCat.getName());     //将name值赋值给对象变量
                catNode.setItem(getCatlist(tbItemCat.getId()));   //如果该数据是父节点还有子节点，调用自身方法，根据id递归查询，直到该数据下没有字节点为止
                arrayList.add(catNode);                           //将数据存放到集合中
                count++;                            //计数
                if(count==18){                     //当到18的时候结束循环
                    break;
                }
            }else{
                arrayList.add(tbItemCat.getName());     //如果不是父节点，下面没有子节点，直接将名称赋值给变量
            }
        }
       return arrayList;
    }
}
