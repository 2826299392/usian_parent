package com.usain.service;

import com.usian.mapper.TbItemMapper;
import com.usian.pojo.TbItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemServiceImp implements ItemService{

    //注入mapper
   @Autowired
   private TbItemMapper tbItemMapper;

    @Override
    public TbItem selectItemInfo(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
    }
}
