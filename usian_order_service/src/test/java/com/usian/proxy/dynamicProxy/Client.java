package com.usian.proxy.dynamicProxy;


import java.lang.reflect.Proxy;

public class Client {
    public static void main(String[] args) {
        Star realStar = new RealStar();   //将真正的角色传递给代理类
        ProxyStar proxyStar = new ProxyStar(realStar);
        //生成代理类的对象  参数一:类的加载器，   参数二：那个接口的代理类  参数三：代理类
        Star star = (Star) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Star.class}, proxyStar);
            star.sing();
    }
}
