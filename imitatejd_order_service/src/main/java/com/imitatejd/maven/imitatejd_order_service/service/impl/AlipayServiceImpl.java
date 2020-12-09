package com.imitatejd.maven.imitatejd_order_service.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.imitatejd.maven.imitatejd_order_service.mapper.OmsOrderMapper;
import lombok.SneakyThrows;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import response.Message;
import response.type.ResponseCodeType;
import service.AlipayService;
import sun.misc.BASE64Encoder;
import type.AlipayResponseType;
import utils.QrcodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 23:00
 * @Description:
 */
@Service
@Component
public class AlipayServiceImpl implements AlipayService {

    private String alipayLogoPos = "classpath:static/alipaylogo.png";

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Value("${spring-redis.order-qrcode-url-key}")
    private String orderQrcodeRediskey;

    @SneakyThrows
    @Override
    public String createQrcode(String orderSn, BigDecimal totalAmount) {
        String qrcode = null;

        // 从redis中获取
        qrcode = redisTemplate.opsForValue().get(orderQrcodeRediskey + orderSn);
        if (StringUtils.isBlank(qrcode)) {
            qrcode = alipayCreateQrcode(orderSn, totalAmount);
            if (!StringUtils.isBlank(qrcode))
                redisTemplate.opsForValue().setIfAbsent(orderQrcodeRediskey + orderSn, qrcode, 30, TimeUnit.SECONDS);
        }
        byte[] qrcodeBytes = QrcodeUtils.createQrcode(qrcode, 200, this.getAlipayLogo());
        return "data:image/jpeg;base64," + new BASE64Encoder().encode(qrcodeBytes);
    }

    @SneakyThrows
    @Override
    public Message queryOrderAlipayStatus(String orderNo) {
        AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
        alipayTradeQueryModel.setOutTradeNo(orderNo);
        alipayTradeQueryRequest.setBizModel(alipayTradeQueryModel);
        AlipayTradeQueryResponse response = alipayClient.execute(alipayTradeQueryRequest);
        if (!response.isSuccess()) {
            if ((AlipayResponseType.TRADE_NOT_EXIST.toString().equals(response.getSubCode())))
                return new Message(ResponseCodeType.TRADE_NOT_EXIST, "未开始交易", true);
            return new Message(ResponseCodeType.UN_KNOW_ERROR, null, false);
        }
        if (AlipayResponseType.WAIT_BUYER_PAY.toString().equals(response.getTradeStatus())){
            String payRoles = response.getBuyerLogonId();
            HashMap<String, String> responseMessage = new HashMap<>();
            responseMessage.put("name", payRoles);
            responseMessage.put("message", "支付中");
            return new Message(ResponseCodeType.WAIT_BUYER_PAY, responseMessage, true);
        }

        if (AlipayResponseType.TRADE_SUCCESS.toString().equals(response.getTradeStatus())) {
            return new Message(ResponseCodeType.TRADE_SUCCESS, "已支付", true);
        }

        if(AlipayResponseType.TRADE_FINISHED.toString().equals(response.getTradeStatus())) {
            return new Message(ResponseCodeType.TRADE_SUCCESS, "交易已关闭", true);
        }

        return null;
    }

    /**
     * 生成alipay的支付二维码
     * @param orderSn
     * @param totalAmount
     * @return
     */
    @SneakyThrows
    private String alipayCreateQrcode(String orderSn, BigDecimal totalAmount) {
        AlipayTradePrecreateRequest alipayTradePrecreateRequest = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel alipayTradePrecreateModel = new AlipayTradePrecreateModel();
        alipayTradePrecreateModel.setSubject(orderSn);
        alipayTradePrecreateModel.setOutTradeNo(orderSn);
        alipayTradePrecreateModel.setTotalAmount(totalAmount.toString());
        alipayTradePrecreateModel.setTimeoutExpress("24h");
        alipayTradePrecreateRequest.setBizModel(alipayTradePrecreateModel);


        AlipayTradePrecreateResponse execute = alipayClient.execute(alipayTradePrecreateRequest);
        if (!execute.isSuccess()) return null;

        return execute.getQrCode();
    }

    private File getAlipayLogo() throws FileNotFoundException {
        return ResourceUtils.getFile(alipayLogoPos);
    }
}
