package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOServiceFeign {

    //注册检验用户是否存在
    @RequestMapping("/service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    Boolean checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag);

    //注册用户信息
    @RequestMapping("/service/sso/userRegister")
    Integer userRegister(TbUser tbUser);

    //用户登录
    @RequestMapping("/service/sso/userLogin")
    Map userLogin(@RequestParam String username,@RequestParam String password);

    //通过登录的时候存到cookie域的token中的数据查询用户信息，在页面展示欢迎XX登录
    @RequestMapping("/service/sso/getUserByToken/{token}")
    TbUser getUserByToken(@PathVariable String token);

    //退出登录清空用户信息
    @RequestMapping("/service/sso/logOut")
    Boolean logOut(@RequestParam String token);
}
