package com.usain.service;

import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
