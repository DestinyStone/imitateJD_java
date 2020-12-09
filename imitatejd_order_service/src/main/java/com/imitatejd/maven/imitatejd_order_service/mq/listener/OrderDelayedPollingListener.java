package com.imitatejd.maven.imitatejd_order_service.mq.listener;

import com.alibaba.fastjson.JSONObject;
import com.imitatejd.maven.imitatejd_order_service.entity.PollingParam;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import mq.OrderDelayedName;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import response.type.ResponseCodeType;
import service.AlipayService;
import service.OmsOrderService;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/5 19:00
 * @Description:
 */
@Component
public class OrderDelayedPollingListener {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OmsOrderService omsOrderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @SneakyThrows
    @RabbitListener(queues = OrderDelayedName.ORDER_QUEUE_DELAYED)
    public void OrderDelayed(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            PollingParam pollingParam = JSONObject.parseObject(message.getBody(), PollingParam.class);
            response.Message response = alipayService.queryOrderAlipayStatus(pollingParam.getOrderNo());
            System.out.println(pollingParam.getCurrentNotifyNumber());
            // 如果已支付， 则修改订单号为 1
            if (response.getResponseCode() == ResponseCodeType.TRADE_SUCCESS) {
                omsOrderService.updateOrderStatus(pollingParam.getOrderNo(), 1);
                channel.basicReject(deliveryTag, false);
                return;
            }

            // 如果超出重试次数 则关闭订单
            if (pollingParam.isExceedMaxNotifyNumber()) {
                omsOrderService.updateOrderStatus(pollingParam.getOrderNo(), 5);
                channel.basicReject(deliveryTag, false);
                return;
            }

            // 重新发送
            pollingParam.setCurrentNotifyNumber(pollingParam.getCurrentNotifyNumber() + 1);
            rabbitTemplate.convertAndSend(OrderDelayedName.ORDER_EXCHANGE_DELAYED, "", JSONObject.toJSONString(pollingParam), message1 -> {
                MessageProperties messageProperties = message1.getMessageProperties();
                messageProperties.setDelay(pollingParam.getDelayedTime());
                return message1;
            });
            channel.basicAck(deliveryTag, false);

        }catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(deliveryTag, false);
        }
    }
}
