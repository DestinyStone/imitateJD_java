package com.imitatejd.maven.imitatejd_order_service.config;

import com.imitatejd.maven.imitatejd_order_service.entity.PollingParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/5 20:54
 * @Description:
 */
@Configuration
public class PollingParamConfig {

    @Bean
    public PollingParam pollingParam() {
        PollingParam pollingParam = new PollingParam();

        HashMap<String, Object> notifyInterval = new HashMap<>();
        // 订单有效时间是48小时， 将48小时划分成不等的11份

        /** 测试数据
        notifyInterval.put("1", 2000 );   // 2分钟
        notifyInterval.put("2", 2000);   // 2分钟
        notifyInterval.put("3", 2000);   // 2分钟
        notifyInterval.put("4", 2000);   // 2分钟
        notifyInterval.put("5", 2000);   // 2分钟
        notifyInterval.put("6", 2000);   // 2分钟

        */
        notifyInterval.put("1", 1000 * 60 * 2);   // 2分钟
        notifyInterval.put("2", 1000 * 60 * 4);   // 4分钟
        notifyInterval.put("3", 1000 * 60 * 8);   // 8分钟
        notifyInterval.put("4", 1000 * 60 * 15);   // 15分钟
        notifyInterval.put("5", 1000 * 60 * 30);   // 30分钟
        notifyInterval.put("6", 1000 * 60 * 60);   // 1 小时
        notifyInterval.put("7", 1000 * 60 * 60 * 1.5);   // 1.5小时
        notifyInterval.put("8", 1000 * 60 * 60 * 3);   // 3小时
        notifyInterval.put("9", 1000 * 60 * 60 * 6);   // 2小时
        notifyInterval.put("10", 1000 * 60 * 60 * 12);   // 2小时
        notifyInterval.put("11", 1000 * 60 * 60 * 24);   // 2小时
        pollingParam.setNotifyInterval(notifyInterval);
        pollingParam.setCurrentNotifyNumber(0);
        pollingParam.setMaxNotifyNumber(notifyInterval.entrySet().size());
        return pollingParam;
    }
}
