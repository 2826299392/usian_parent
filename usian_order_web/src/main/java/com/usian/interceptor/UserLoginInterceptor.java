package com.usian.interceptor;

import com.usian.feign.SSOServiceFeign;
import com.usian.pojo.TbUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在结算之前判断用户是否登录
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired    //注入登录的接口
    private SSOServiceFeign ssoServiceFeign;

    /**
     *方法执行前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取coolie里面的用户的token
        String token = request.getParameter("token");
        //对用户的token进行判断为空的话不放行
        if(StringUtils.isBlank(token)){
            return false;
        }

        //根据token查询redis中该用户是否失效
        TbUser user = ssoServiceFeign.getUserByToken(token);
        if(user==null){  //判断redis中的用户如果已经失效就不放行
            return false;
        }
        return true;
    }

    /**
     *方法执行后跳转页面前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     *方法执行后跳转页面后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
