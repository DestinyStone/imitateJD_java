package com.imitatejd.maven.imitatejd_item_controller;

import com.imitatejd.maven.imitatejd_order_controller.annon.EnableShiro;
import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableShiro
@Import(FdfsClientConfig.class)
public class ImitatejdItemControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImitatejdItemControllerApplication.class, args);
    }

}
