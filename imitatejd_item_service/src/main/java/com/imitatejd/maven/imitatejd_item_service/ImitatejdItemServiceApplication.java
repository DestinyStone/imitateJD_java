package com.imitatejd.maven.imitatejd_item_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.imitatejd.maven.imitatejd_item_service.mapper")
public class ImitatejdItemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdItemServiceApplication.class, args);
    }

}
