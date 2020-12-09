package com.imitatejd.maven.imitatejd_passport_controller.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/17 15:09
 * @Description:
 */
@RestController
@CrossOrigin
public class ErrorController {

    @GetMapping("/errorHandler/{message}")
    @ApiOperation("filter中的异常会指向该方法")
    public String test(@PathVariable("message") String message) {
        System.out.println(message);
        System.out.println("3333333333333333333333333333");
        return "33333333";
    }
}
