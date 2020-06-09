package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/service/sso")
public class SSOController {

    @Autowired
    private SSOService ssoService;

    //注册校验用户是否存在
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Boolean checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag){
        return ssoService.checkUserInfo(checkValue,checkFlag);
    }

    //注册用户信息
    @RequestMapping("/userRegister")
    public Integer userRegister(@RequestBody TbUser tbUser){
        return ssoService.userRegister(tbUser);
    }

    //用户登录
    @RequestMapping("/userLogin")
    public Map userLogin(@RequestParam String username,@RequestParam String password){
        return ssoService.userLogin(username,password);
    }

    //通过登录的时候存到cookie域的token中的数据查询用户信息，在页面展示欢迎XX登录
    @RequestMapping("/getUserByToken/{token}")
    public TbUser getUserByToken(@PathVariable String token){
        return ssoService.getUserByToken(token);
    }

    //退出登录清空用户信息
    @RequestMapping("/logOut")
    public Boolean logOut(@RequestParam String token){
        return ssoService.logOut(token);
    }
}
