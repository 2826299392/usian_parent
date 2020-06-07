package com.usian.mapper;

import com.usian.pojo.SearchItem;

import java.util.List;

public interface SearchMapper {
    List<SearchItem> getItemList();

    SearchItem addDocement(String itemID);
}
