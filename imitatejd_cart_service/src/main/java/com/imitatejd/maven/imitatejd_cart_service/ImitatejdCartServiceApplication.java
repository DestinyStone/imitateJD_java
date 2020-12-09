package com.imitatejd.maven.imitatejd_cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.imitatejd.maven.imitatejd_cart_service.mapper")
public class ImitatejdCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdCartServiceApplication.class, args);
    }

}
