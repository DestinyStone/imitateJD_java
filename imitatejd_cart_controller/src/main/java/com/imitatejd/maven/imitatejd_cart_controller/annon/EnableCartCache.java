package com.imitatejd.maven.imitatejd_cart_controller.annon;

import java.lang.annotation.*;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/7 09:39
 * @Description: 开启购物车的缓存
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCartCache {
}
