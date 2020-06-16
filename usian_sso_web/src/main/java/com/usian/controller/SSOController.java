package com.usian.controller;

import com.usian.CartServiceFeign;
import com.usian.feign.SSOServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbUser;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {


    @Autowired      //注入接口
    private SSOServiceFeign ssoServiceFeign;

    @Autowired    //注入购物车接口
    private CartServiceFeign cartServiceFeign;

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
    public Result userLogin(@RequestParam String username, @RequestParam String password,HttpServletRequest request, HttpServletResponse response){

        Map map = ssoServiceFeign.userLogin(username,password);
        //判断用户注册的时候不为空
        if(map!=null){       //用户登录的时候同步cookie中的购物车信息到redis中

            //1、获取cookie中的购物车信息
            String cartJson = CookieUtils.getCookieValue(request, "CART_COOKIE_KEY", true);
            //判断cookie中获取的数据是否为空，不为空的话才进行同步，为空的话直接进行登录
            if(StringUtils.isNotBlank(cartJson)){
                Map<String,TbItem> cookieCart = JsonUtils.jsonToMap(cartJson, TbItem.class);   //获取cookie中的购物车

                String userId = (String) map.get("userid");//获取注册的时候返回的map中用户的id

                Map<String, TbItem> redisUserIdCart = cartServiceFeign.getRedisUserIdCart(userId);  //根据该用户的id获取redis中的购物车

                Set<String> keys = cookieCart.keySet();  //获取cookie购物车中的所有key的集合

                for (String key : keys) {     //遍历所有的key
                    //redis的购物车和cookie中的购物车如果都能通过该key获取到商品，只修改redis中商品的数量
                    TbItem cookieTbItem = cookieCart.get(key);   //根据key获取ciikie中商品的信息
                    TbItem redisTbItem = redisUserIdCart.get(key);   //根据key获取redis中购物车的商品信息

                    if(redisTbItem!=null){    //判断redis获取的不为空的话，cookie和redis的商品数量相加，，同步到redis中
                        redisTbItem.setNum(cookieTbItem.getNum()+redisTbItem.getNum());    //同步到redis中修改redis中商品的数量相加
                        redisUserIdCart.put(redisTbItem.getId().toString(),redisTbItem);  //在将同步好的商品信息添加到redis购物车中
                    }else{
                        redisUserIdCart.put(cookieTbItem.getId().toString(),cookieTbItem);  //如果没有商品id相同，就说明Redis中没有cookie中的该商品，将从cookie中获取的商品添加到redis购物车中
                    }
                }

                cartServiceFeign.addCartToRedis(userId,redisUserIdCart);  //将购物车添加到redis中
                CookieUtils.deleteCookie(request,response,"CART_COOKIE_KEY");  //清空cookie中的数据

            }
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
