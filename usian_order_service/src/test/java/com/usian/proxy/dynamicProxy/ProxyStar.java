package com.usian.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * InvocationHandler:接口通过invoke方法调用真实角色       动态代理
 * 好处：可以代理任意类型的对象
 *       代理类中没用重复的方法
 */

public class ProxyStar implements InvocationHandler {

    private Object realStar;   //定义真实角色
    public ProxyStar(Object object){  //接收真正的角色实现类赋值
        this.realStar=object;
    }

    @Override           //参数1：代理类     参数二 ：方法    参数三： 参数
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("面谈，签合同，预付款，订机票");
        Object result = method.invoke(realStar, args);  //反射调用真实角色的方法
        System.out.println("收尾款");
        return null;
    }
}
