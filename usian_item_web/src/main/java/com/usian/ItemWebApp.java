package com.usian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients //扫描feign工程接口
@EnableDiscoveryClient  //容许想注册中心注册
@SpringBootApplication
public class ItemWebApp {
    public static void main(String[] args) {
        SpringApplication.run(ItemWebApp.class,args);
    }
}
