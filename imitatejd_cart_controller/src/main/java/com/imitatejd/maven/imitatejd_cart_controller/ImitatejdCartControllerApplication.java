package com.imitatejd.maven.imitatejd_cart_controller;

import com.imitatejd.maven.imitatejd_order_controller.annon.EnableShiro;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableShiro
public class ImitatejdCartControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdCartControllerApplication.class, args);
    }

}
