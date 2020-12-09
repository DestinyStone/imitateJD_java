package com.imitatejd.maven.imitatejd_item_controller.cache.aop;

import com.alibaba.fastjson.JSONObject;
import com.imitatejd.maven.imitatejd_item_controller.annon.BasicEnableCache;
import org.apache.dubbo.common.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import response.Message;

import java.lang.reflect.Method;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/28 08:38
 * @Description: 实现基本的缓存
 */
@Aspect
@Component
public class BasicEnableCacheAop {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.imitatejd.maven.imitatejd_item_controller.annon.BasicEnableCache)")
    public void pointcut(){}

    @Around("pointcut()")
    public Message around(ProceedingJoinPoint joinPoint) throws Throwable {
        String redisKey = getKey(joinPoint);

        String redisCache = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(redisCache)) {
            Message responseMessage = (Message)joinPoint.proceed();
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(responseMessage));
            return responseMessage;
        }
        Message messageCache = JSONObject.parseObject(redisCache, Message.class);
        return messageCache;
    }

    private String getKey(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取tableName和idName 以及 idValue
        String idValue = joinPoint.getArgs()[0].toString();

        Method method = joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName(), idValue.getClass());
        BasicEnableCache basicEnableCache = method.getAnnotation(BasicEnableCache.class);
        String idName = basicEnableCache.idName();
        String tableName = basicEnableCache.tableName();

        return tableName + ":" + idName + ":" + idValue;
    }

}
