package com.usian.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class CatNode implements Serializable{
    @JsonProperty("n")  //将name变量赋值后以json格式响应给前台定义的n
    private String name;

    @JsonProperty("i")  //将item变量赋值后以json格式响应给前台定义的i
    private List<?> item;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<?> getItem() {
        return item;
    }

    public void setItem(List<?> item) {
        this.item = item;
    }
}
