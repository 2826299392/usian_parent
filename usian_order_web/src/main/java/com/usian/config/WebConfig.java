package com.usian.config;

import com.usian.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 实现注入拦截器的类
 */
@Component
public class WebConfig implements WebMvcConfigurer {
    @Autowired   //注入拦截器
    private UserLoginInterceptor userLoginInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //告诉拦截的位置是哪个类
        InterceptorRegistration registration = registry.addInterceptor(userLoginInterceptor);
        //告诉拦截的是那些路径
        registration.addPathPatterns("/frontend/order/**");
    }
}
