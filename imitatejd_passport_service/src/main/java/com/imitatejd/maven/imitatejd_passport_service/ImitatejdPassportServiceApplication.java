package com.imitatejd.maven.imitatejd_passport_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.imitatejd.maven.imitatejd_passport_service.mapper")
public class ImitatejdPassportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdPassportServiceApplication.class, args);
    }

}
