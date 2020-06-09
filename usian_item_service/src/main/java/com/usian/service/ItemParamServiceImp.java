package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.pojo.TbItemParamItem;
import com.usian.pojo.TbItemParamItemExample;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImp implements ItemParamService{

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;   //表示商品的存redis的时候key名

    @Value("${BASE}")
    private String BASE;       //表示商品详情信息存redis的时候key名

    @Value("${DESC}")
    private String DESC;        //描述信息存redis的时候key名

    @Value("${PARAM}")
    private String PARAM;      //表示规格参数的存redis的时候key名

    @Value("${ITEM_INFO_EXPIRE}")
    private Long ITEM_INFO_EXPIRE;   //存redis的时候设定的失效时间

    @Value("${SETNX_PARAM_LOCK_KEY}")
    private String SETNX_PARAM_LOCK_KEY;   //存redis的时候设定的失效时间

    @Autowired  //注入redis工具类
    private RedisClient redisClient;

    //注入mapper
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;


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

    //商品详情页的规格参数查询
    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId){
        //1、存缓存中查询信息     根据key查询value
        TbItemParamItem tbItemParamItem = (TbItemParamItem) redisClient.get(ITEM_INFO+":"+itemId+":"+PARAM);
        if(tbItemParamItem!=null){    //判断数据是否查到
            return tbItemParamItem;
        }

        if(redisClient.setnx(SETNX_PARAM_LOCK_KEY+":"+itemId,itemId,30L)){
            //2、给数据设置有效时间，将查询到的商品数据添加到redis缓存中
            TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();      //条件工具类
            TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
            criteria.andItemIdEqualTo(itemId);                                           //封装条件
            List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
            if(tbItemParamItems!=null && tbItemParamItems.size()>0){      //判断数据
                redisClient.set(ITEM_INFO+":"+itemId+":"+PARAM,tbItemParamItems.get(0));    //存到redis中
                redisClient.expire(ITEM_INFO+":"+itemId+":"+PARAM,ITEM_INFO_EXPIRE);       //保证数据为最新，设置失效时间
            }else {
                redisClient.set(ITEM_INFO+":"+itemId+":"+PARAM,null);    //解决缓存穿透，空值也进行缓存
                redisClient.expire(ITEM_INFO+":"+itemId+":"+PARAM,ITEM_INFO_EXPIRE);       //保证数据为最新，设置失效时间
            }
            redisClient.del(SETNX_PARAM_LOCK_KEY+":"+itemId);
            return null;
        }else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectTbItemParamItemByItemId(itemId);
        }
    }
}
