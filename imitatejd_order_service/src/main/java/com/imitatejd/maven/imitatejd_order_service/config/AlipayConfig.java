package com.imitatejd.maven.imitatejd_order_service.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 23:00
 * @Description:
 */
@Configuration
public class AlipayConfig {
    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.gateway-url}")
    private String gateWayUrl;

    @Value("${alipay.merchant-private-key}")
    private String privateKey;

    @Value("${alipay.alipay-public-key}")
    private String publicKey;

    @Value("${alipay.sign-type}")
    private String signType;

    private String charset = "utf-8";

    @Bean
    public AlipayClient alipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(gateWayUrl, appId, privateKey, "json", charset, publicKey, signType);
        return alipayClient;
    }
}
