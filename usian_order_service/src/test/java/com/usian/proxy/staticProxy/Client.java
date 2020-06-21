package com.usian.proxy.staticProxy;

public class Client {
	public static void main(String[] args) {
		Star real = new RealStar();   //创建周杰伦对象可以用接口接收
		Star proxy = new ProxyStar(real);  //创建代理对象
		
		proxy.confer();
		proxy.signContract();
		proxy.bookTicket();  //调用代理和本人都能做的事情
		proxy.sing();  //通过代理调用唱歌的周杰伦    静态代理
		
		proxy.collectMoney();
		
	}
}