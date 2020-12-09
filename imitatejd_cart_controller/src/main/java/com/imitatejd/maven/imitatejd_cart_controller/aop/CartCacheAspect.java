package com.imitatejd.maven.imitatejd_cart_controller.aop;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/7 09:40
 * @Description:
 */
@Aspect
@Component
public class CartCacheAspect {

    @Pointcut("@annotation(com.imitatejd.maven.imitatejd_cart_controller.annon.EnableCartCache)")
    public void pointcut(){}

    @Around("pointcut()")
    public void around() {

    }
}
