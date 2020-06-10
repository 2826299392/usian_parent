package com.usian.controller;

import com.usian.CartServiceFeign;
import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/frontend/cart")
public class CartController {

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    private Integer CART_COOKIE_EXPIRE;

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    //添加商品到购物车
    @RequestMapping("/addItem")
    public Result addItem(Long itemId,
                          String userId,
                          @RequestParam(defaultValue = "1")Integer num,
                          HttpServletRequest request,
                          HttpServletResponse response){
        try {
              //1、判断userId是否为空也就是用户是在登录的状态还是在未登录的状态添加的购物车
               if(StringUtils.isBlank(userId)){  /******为空未登录情况下添加购物车********/
                      //1、从cookie中获取购物车信息,就是购物车里面的商品列表信息
                      Map<String,TbItem> cart = getCookieCart(request);

                      //2、添加商品到购物车
                      addItemToCart(cart,itemId,num);

                      //3、将更新了的购物车从新添加到页面的cookie域中
                      addCartToCookie(request,response,cart);

               }else { /******登录情况下添加购物车到redis中********/
                     //1、从redis中获取购物车
                      Map<String , TbItem> cart = getRedisCart(userId);
                      //2、将商品信息添加到我们的购物车中
                      addItemToCart(cart,itemId,num);
                      //3、将我们的购物车保存到redis中
                      Boolean s = cartServiceFeign.addCartToRedis(userId,cart);
                      if(!s){
                          return Result.error("添加失败");
                      }
               }
               return Result.ok();

        }catch (Exception e){
            e.printStackTrace();
            return Result.error("添加购物车失败");
        }
    }

    //获取页面cookie域里面的购物车信息
    private Map<String,TbItem> getCookieCart(HttpServletRequest request) {
        //获取cookie中购物车里面的    商品信息，
        String cartJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);
        //如果不为空的话说明有商品
        if(StringUtils.isNotBlank(cartJson)){
            Map<String,TbItem> map = JsonUtils.jsonToMap(cartJson, TbItem.class);  //将商品以map的形式存储
            return map;
        }
        return new HashMap<String,TbItem>();   //如果没有商品信息，返回一个空的map集合存储添加商品时候存储商品信息
    }

    //获取redis中的购物车信息
    private Map<String, TbItem> getRedisCart(String userId) {
        //1、从redis中获取购物车信息
        Map<String,TbItem> cart = cartServiceFeign.getRedisUserIdCart(userId);
        //判断如果没有查到该用户的购物车信息，就返回一个空的购物车使用
        if (cart==null || cart.size()==0){
            return new HashMap<String,TbItem>();
        }
        return cart;  //返货购物车的商品信息
    }


    //实现添加商品到map集合的购物车中
    private void addItemToCart(Map<String, TbItem> cart, Long itemId, Integer num) {
           //1、先从购物车中查询该商品是否存在，存在的话添加数量
           TbItem tbItem = cart.get(itemId.toString());
           if (tbItem!=null){
               tbItem.setNum(tbItem.getNum()+num);  //在原有的数量添加数量1
           }else{
               tbItem = itemServiceFeignClient.selectItemInfo(itemId); //如果购物车中没有该商品，查询该商品
               tbItem.setNum(num);   //初始添加数量为1
           }
           cart.put(itemId.toString(),tbItem);  //将新数据添加到map中购物车中
    }

    //将更新后的购物车添加到页面的cookie域中
    private void addCartToCookie(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Map<String, TbItem> cart) {
        //1、获去更新后购物车的数据信息
        String cartJson = JsonUtils.objectToJson(cart);
        CookieUtils.setCookie(request,response,CART_COOKIE_KEY,cartJson,CART_COOKIE_EXPIRE,true);
    }

    //查看购物车信息
    @RequestMapping("/showCart")
    public Result showCart(String userId,
                           HttpServletRequest request,
                           HttpServletResponse response){
        try {
             List<TbItem> list = new ArrayList<>();
             if(StringUtils.isBlank(userId)){
                 /******为空未登录情况下添加购物车********/
                 //1、获取购物车map
                 Map<String, TbItem> cart = getCookieCart(request);
                 Set<String> keys = cart.keySet();  //获取map里面所有key的集合数组
                 for (String key: keys) {
                     list.add(cart.get(key));  //根据key获取value存到list响应给前台
                 }
             }else {
                 // 在用户已登录的状态
             }
            return Result.ok(list);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("查询失败");
        }
    }

    //修改cookie里的购物车中的数据的数量，增加数量
    @RequestMapping("/updateItemNum")
    public Result updateItemNum(Integer num,Long itemId,String userId,
                                HttpServletRequest request,
                                HttpServletResponse response){
        try {
            if (StringUtils.isBlank(userId)){    /******为空未登录情况下添加购物车********/
                //1、获取购物车
                Map<String, TbItem> cart = getCookieCart(request);

                //2、根据商品id获取购物车里面的商品,修改商品数量,在添加到map中覆盖，
                TbItem tbItem = cart.get(itemId.toString());
                tbItem.setNum(num);
                cart.put(itemId.toString(),tbItem);
                addCartToCookie(request,response,cart);  //调用将购物车添加到cookie域中的方法
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("修改失败");
        }
    }

    //删除购物车中商品信息
    @RequestMapping("/deleteItemFromCart")
    public Result deleteItemFromCart(Long itemId,String userId,
                                     HttpServletRequest request,
                                     HttpServletResponse response){
        try { /******为空未登录情况下添加购物车********/
              if (StringUtils.isBlank(userId)){
                  Map<String, TbItem> cart = getCookieCart(request);  //获取购物车
                  cart.remove(itemId.toString());      //根据itemId删除购物车中的商品信息
                  addCartToCookie(request,response,cart);

              }else {
                   //已登录
              }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }

}
