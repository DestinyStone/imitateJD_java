package com.imitatejd.maven.imitatejd_search_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.imitatejd.maven.imitatejd_search_service.mapper")
public class ImitatejdSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdSearchServiceApplication.class, args);
    }

}
