package com.usian.proxy.staticProxy;

public class ProxyStar implements Star {
	
	private Star star;
	
	public ProxyStar(Star star) {     //代理类静态代理
		super();
		this.star = star;
	}

	public void bookTicket() {
		System.out.println("ProxyStar.bookTicket()"); //订票
	}

	public void collectMoney() {
		System.out.println("ProxyStar.collectMoney()"); //收钱
	}

	public void confer() {
		System.out.println("ProxyStar.confer()");  //面谈
	}

	public void signContract() {
		System.out.println("ProxyStar.signContract()"); //签合同
	}

	public void sing() {
		star.sing();   //唱歌
	}

}