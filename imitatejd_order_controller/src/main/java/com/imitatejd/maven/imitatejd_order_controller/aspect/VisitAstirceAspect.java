package com.imitatejd.maven.imitatejd_order_controller.aspect;

import bean.UmsMember;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import response.Message;
import response.type.ResponseCodeType;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 21:40
 * @Description:
 */
@Aspect
@Component
public class VisitAstirceAspect {

    private final String VISIT_USER_KEY = "visit:userId:";

    @Pointcut("@annotation(com.imitatejd.maven.imitatejd_order_controller.annon.VisitAstrice)")
    public void pointcut(){}

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("pointcut()")
    public Message around(ProceedingJoinPoint proceedingJoinPoint) {
        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();
        try {
            Boolean isAstirce = redisTemplate.opsForValue().setIfAbsent(VISIT_USER_KEY + userId, "1", 20, TimeUnit.SECONDS);
            if (isAstirce) {
                Message proceed = (Message)proceedingJoinPoint.proceed();
                return proceed;
            } else {
                return new Message(ResponseCodeType.OFTEN_COMMIT, "表单提交频繁", true);
            }
        }catch (Throwable e) {
            redisTemplate.delete(VISIT_USER_KEY + userId);
            e.printStackTrace();
            return new Message(ResponseCodeType.UN_KNOW_ERROR, null, false);
        }
    }
}
