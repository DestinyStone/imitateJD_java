package com.imitatejd.maven.imitatejd_order_controller.annon;

import config.ShiroConfig;
import execption.GobalExecptionHandler;
import execption.PassportExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/9 14:16
 * @Description: 启动类上添加该注解，则开启shiro配置
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ShiroConfig.class, GobalExecptionHandler.class, PassportExceptionHandler.class})
public @interface EnableShiro {
}
