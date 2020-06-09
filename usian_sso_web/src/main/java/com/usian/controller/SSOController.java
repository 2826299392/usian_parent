package com.usian.controller;

import com.usian.feign.SSOServiceFeign;
import com.usian.pojo.TbUser;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {


    @Autowired      //注入接口
    private SSOServiceFeign ssoServiceFeign;

    //注册校验用户是否已经存在
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag){
       Boolean s = ssoServiceFeign.checkUserInfo(checkValue,checkFlag);
       if(s){
           return Result.ok();
       }
       return Result.error("用户已存在，请重新注册用户");
    }

    //注册用户信息
    @RequestMapping("/userRegister")
    public Result userRegister(TbUser tbUser){
        Integer num = ssoServiceFeign.userRegister(tbUser);
        if(num==1){
            return Result.ok();
        }
        return Result.error("注册失败");
    }

    //用户登录
    @RequestMapping("/userLogin")
    public Result userLogin(@RequestParam String username,@RequestParam String password){
        Map map = ssoServiceFeign.userLogin(username,password);
        if(map!=null){
            return Result.ok(map);    //响应到前台存到cookie域的token中
        }
        return Result.error("登录失败");
    }

    //通过登录的时候存到cookie域的token中的数据查询用户信息，在页面展示欢迎XX登录
    @RequestMapping("/getUserByToken/{token}")
    public Result getUserByToken(@PathVariable String token){
        TbUser tbUser = ssoServiceFeign.getUserByToken(token);
        if (tbUser!=null){
            return Result.ok();
        }
        return Result.error("用户查询失败");
    }

    //退出登录清空用户信息
    @RequestMapping("/logOut")
    public Result logOut(@RequestParam String token){
        Boolean s = ssoServiceFeign.logOut(token);
        if(s){
            return Result.ok();
        }
        return Result.error("退出失败");
    }
}
