package com.usian.proxy.dynamicProxy;

public class RealStar implements Star {  //实现类

	public void bookTicket() {
		System.out.println("RealStar.bookTicket()");//订票
	}

	public void collectMoney() {
		System.out.println("RealStar.collectMoney()");//收钱
	}

	public void confer() {
		System.out.println("RealStar.confer()"); //面谈
	}

	public void signContract() {
		System.out.println("RealStar.signContract()"); //签合同
	}

	public void sing() {
		System.out.println("RealStar(周杰伦本人).sing()");  //唱歌
	}
	
	
	
}