package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.redis.RedisClient;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@Transactional
public class ContentServiceImp implements ContentService {
    //获取配置文件中定义的图片信息宽高，根据属性注入
    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Value("${PORTAL_AD_KEY}")
    private String PORTAL_AD_KEY;

    @Autowired   //注入redis缓存
    private RedisClient redisClient;

    //注入mapper
    @Autowired
    private TbContentMapper tbContentMapper;

    //分页查询内容信息
    @Override
    public PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId) {
        PageHelper.startPage(page,rows);   //分页，从第几页查，每页展示几条数据
        TbContentExample tbContentExample = new TbContentExample();  //创建sql语句工具类
        tbContentExample.setOrderByClause("updated DESC");
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();  //创建where条件
        criteria.andCategoryIdEqualTo(categoryId);  //添加条件
        List<TbContent> tbContentList = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);  //查询
        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContentList);  //将数据添加到分页pageinfo中
        PageResult pageResult = new PageResult();  //创建分页工具类
        pageResult.setResult(pageInfo.getList());
        pageResult.setPageIndex(pageInfo.getPageNum());
        pageResult.setTotalPage(Long.valueOf(pageInfo.getPages()));

        return pageResult;
    }

    //内容添加
    @Override
    public Integer insertTbContent(TbContent tbContent) {
        //补齐字段
        TbContentExample tbContentExample = new TbContentExample();
        tbContent.setUpdated(new Date());
        tbContent.setCreated(new Date());
        //缓存同步，在添加的时候删除redsi旧的缓存数据，使在查询的时候，先从数据库中查询新的数据，在保存到redis的缓存中
        redisClient.hdel(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        return tbContentMapper.insertSelective(tbContent);
    }

    @Override
    public Integer deleteContentByIds(Long ids) {
        //缓存同步，在删除的时候删除redsi旧的缓存数据，使在查询的时候，先从数据库中查询新的数据，在保存到redis的缓存中
        redisClient.hdel(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        return tbContentMapper.deleteByPrimaryKey(ids);
    }

    //大广告查询 配置文件中定义了默认id查询
    @Override
    public List<AdNode> selectFrontendContentByAD() {
        //1、redis缓存中查询      AD_CATEGORY_ID.toString()默认的id查询它下面所有的数据
        List<AdNode> adNodeList = (List<AdNode>) redisClient.hget(PORTAL_AD_KEY, AD_CATEGORY_ID.toString());
        if(adNodeList!=null){
            return adNodeList;   //redis中有的话直接返回
        }

        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(AD_CATEGORY_ID);
        List<TbContent> tbContentList = tbContentMapper.selectByExample(tbContentExample);
        List<AdNode> adNodeArrayList = new ArrayList<>();
        for(TbContent tbContent:tbContentList){
            AdNode adNode = new AdNode();
            adNode.setSrc(tbContent.getPic());
            adNode.setSrcB(tbContent.getPic2());
            adNode.setAlt(tbContent.getUrl());
            adNode.setHeight(AD_HEIGHT);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setWidth(AD_WIDTH);
            adNode.setWidthB(AD_WIDTHB);
            adNodeArrayList.add(adNode);
        }
        //从数据库中查完添加到redis缓存中
        redisClient.hset(PORTAL_AD_KEY,AD_CATEGORY_ID.toString(),adNodeArrayList);
        return adNodeArrayList;
    }
}
