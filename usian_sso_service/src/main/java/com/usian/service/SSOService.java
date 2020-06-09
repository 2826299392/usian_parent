package com.usian.service;

import com.usian.pojo.TbUser;

import java.util.Map;

public interface SSOService {
    //注册校验用户是否存在
    Boolean checkUserInfo(String checkValue, Integer checkFlag);

    //注册用户信息
    Integer userRegister(TbUser tbUser);

    //用户登录
    Map userLogin(String username, String password);

    //通过登录的时候存到cookie域的token中的数据查询用户信息，在页面展示欢迎XX登录
    TbUser getUserByToken(String token);

    //退出登录清空用户信息
    Boolean logOut(String token);
}
