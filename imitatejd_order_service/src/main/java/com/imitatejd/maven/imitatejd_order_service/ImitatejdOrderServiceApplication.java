package com.imitatejd.maven.imitatejd_order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.imitatejd.maven.imitatejd_order_service.mapper")
public class ImitatejdOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdOrderServiceApplication.class, args);
    }

}
