package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SSOServiceImp implements SSOService{

    @Autowired
    private TbUserMapper tbUserMapper;

    @Value("${USER_INFO}")
    private String USER_INFO;

    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;

    @Autowired
    private RedisClient redisClient;

    //注册校验用户是否存在
    @Override
    public Boolean checkUserInfo(String checkValue, Integer checkFlag) {

        TbUserExample tbUserExample = new TbUserExample();  //创建sql语句的工具类
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();  //条件

        if (checkFlag==1){  //判断状态 1：根据name查询   2：根据手机号photo查询
            criteria.andUsernameEqualTo(checkValue);
        }else if (checkFlag==2){
            criteria.andPhoneEqualTo(checkValue);
        }

        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);  //查询
        //判断结果，如果查到用户返回false，  如果没有查到返回true用户不存在可以正常注册
        if(tbUsers==null || tbUsers.size()==0){
            return true;
        }
        return false;
    }

    //注册用户信息
    @Override
    public Integer userRegister(TbUser tbUser) {
        //1、为了保证密码的安全性对密码进行加密
        String password = MD5Utils.digest(tbUser.getPassword());
        tbUser.setPassword(password);

        //2、补全空余字段数据
        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());
        return tbUserMapper.insertSelective(tbUser);
    }

    //用户登录
    @Override
    public Map userLogin(String username, String password) {
        //1、在注册的时候密码进行了加密，所以在登录的时候密码也要加密才能对比
        String pwd = MD5Utils.digest(password);

        //2、根据密码和用户进行查询
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(pwd);
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if (tbUsers==null || tbUsers.size()==0){  //判断用户是否存在，没有登录失败
            return null;
        }

        //2、获取用户信息存到redis中，保证密码安全，密码设为空不显示
        TbUser tbUser = tbUsers.get(0);
        tbUser.setPassword(null);

        String token = UUID.randomUUID().toString();   //设置一个随机的token
        redisClient.set(USER_INFO+":"+token,tbUser);   //存到redis中
        redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);   //设置失效时间

        Map<String, Object> map = new HashMap<>();   //分装到map中响应给前台存到cookie域中的token
        map.put("token",token);
        map.put("username",tbUser.getUsername());
        map.put("userid",tbUser.getId().toString());
        return map;
    }

    //通过登录的时候存到cookie域的token中的数据查询用户信息，在页面展示欢迎XX登录
    @Override
    public TbUser getUserByToken(String token) {
        //1、存redis中查询用户信息
        TbUser tbuser = (TbUser) redisClient.get(USER_INFO + ":" + token);   //查询redis中的数据
        if(tbuser!=null){
            redisClient.expire(USER_INFO + ":" + token,SESSION_EXPIRE);      //不为空的话，重新设置失效时间
            return tbuser;
        }
        return null;
    }

    //退出登录清空用户信息
    @Override
    public Boolean logOut(String token) {
        return redisClient.del(USER_INFO + ":" + token);  //删除redis中的用户数据
    }
}
