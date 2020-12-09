package com.imitatejd.maven.imitatejd_item_controller.annon;

import java.lang.annotation.*;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/28 08:33
 * @Description: 开启缓存， key =  tableName:idName:idValue 其中tableName， idName从参数中获取, idValue在aop中获取
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BasicEnableCache {
    String tableName();
    String idName();
}
