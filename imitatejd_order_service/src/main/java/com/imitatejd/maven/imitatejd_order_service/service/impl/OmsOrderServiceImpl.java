package com.imitatejd.maven.imitatejd_order_service.service.impl;

import bean.*;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.imitatejd.maven.imitatejd_order_service.entity.PollingParam;
import com.imitatejd.maven.imitatejd_order_service.mapper.*;
import mq.OrderDelayedName;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import response.Message;
import response.type.ResponseCodeType;
import service.OmsOrderService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 12:52
 * @Description:
 */
@Service
@Component
public class OmsOrderServiceImpl implements OmsOrderService {

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    private PmsSkuRepositoryMapper pmsSkuRepositoryMapper;

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.redis.orderno-id}")
    private String orderNoId;

    @Value("${order.expire}")
    private String orderExpireTime;

    @Autowired
    private PollingParam pollingParam;

    @Override
    public Message genOrder(String userId, String[] skuIds, String addressId) {
        List<OmsCartItem> omsCartItemList =  omsCartItemMapper.selectBySkuIdsAndUserId(userId, skuIds);

        if (omsCartItemList.size() == 0)
            return new Message(ResponseCodeType.NO_COMMODITY_STORE, "购物车异常", true);

        // 查看所有库存是否都满足
        if (!hasRepository(omsCartItemList))
            return new Message(ResponseCodeType.NO_COMMODITY_STORE, "商品库存不足", true);
        // 消减库存
        lockRepository(omsCartItemList);

        // 获取用户选择的发货地址
        UmsMemberReceiveAddress umsMemberReceiveAddressQuery = UmsMemberReceiveAddress.builder().id(addressId).build();
        UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddressQuery);

        // 生成订单信息
        OmsOrder omsOrder = OmsOrder.builder()
                .memberId(userId)
                .orderSn(generateOrderSn())
                .createTime(new Date())
                .totalAmount(computeTotalMoney(omsCartItemList))
                .memberUsername(umsMemberReceiveAddress.getName())
                .receiverName(umsMemberReceiveAddress.getName())
                .receiverPhone(umsMemberReceiveAddress.getPhoneNumber())
                .receiverProvince(umsMemberReceiveAddress.getProvince())
                .receiverCity(umsMemberReceiveAddress.getCity())
                .receiverRegion(umsMemberReceiveAddress.getRegion())
                //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
                .status(0)
                .build();

        omsOrderMapper.insertSelective(omsOrder);

        // 生成订单 Item
        List<OmsOrderItem> omsOrderItemListQuery = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItemList) {
            OmsOrderItem omsOrderItem = OmsOrderItem.builder()
                    .orderId(omsOrder.getId())
                    .orderSn(omsOrder.getOrderSn())
                    .productSkuId(omsCartItem.getProductSkuId())
                    .productPrice(omsCartItem.getPrice())
                    .productQuantity(omsCartItem.getQuantity())
                    .build();
            omsOrderItemListQuery.add(omsOrderItem);
        }
        omsOrderItemMapper.insertList(omsOrderItemListQuery);

        // 删除购物车数据
        for (OmsCartItem omsCartItem : omsCartItemList) {
            omsCartItemMapper.deleteByPrimaryKey(omsCartItem);
        }

        // 发送延迟队列检测订单是否超时
        sendDelayedCheckOrder(omsOrder.getOrderSn());

        HashMap<String, String> result = new HashMap<>();
        result.put("orderNo", omsOrder.getOrderSn());
        return new Message(ResponseCodeType.SUCCESS, result, true);
    }

    private void sendDelayedCheckOrder(String orderSn) {
        pollingParam.setOrderNo(orderSn);
        pollingParam.setCurrentNotifyNumber(1);  // 第一次发送

        rabbitTemplate.convertAndSend(OrderDelayedName.ORDER_EXCHANGE_DELAYED, "", JSONObject.toJSONString(pollingParam), message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setDelay(pollingParam.getDelayedTime());
            return message;
        });
    }

    @Override
    public Message getOrderDetail(String orderNo, String userId) {
        OmsOrder omsOrderQuery = OmsOrder.builder().orderSn(orderNo).memberId(userId).build();
        OmsOrder omsOrder = omsOrderMapper.selectOne(omsOrderQuery);

        //计算订单超时时间
        Long expireTime = computedOrderExpireTime(omsOrder.getCreateTime());
        HashMap<String, Object> result = new HashMap<>();
        result.put("omsOrder", omsOrder);
        result.put("omsOrderExpireTime", expireTime);
        return new Message(ResponseCodeType.SUCCESS, result, true);
    }

    @Override
    public Message updateOrderStatus(String orderNo, int status) {

        // 如果是无效订单， 只有订单状态为 0 -> 未付款 才能更改
        if (status == 5) {
            OmsOrder omsOrderQuery = OmsOrder.builder().orderSn(orderNo).build();
            OmsOrder omsOrderResult = omsOrderMapper.selectOne(omsOrderQuery);
            if (omsOrderResult.getStatus() != 0) {
                return new Message(ResponseCodeType.UN_KNOW_ERROR, "更新异常", false);
            }
        }
        omsOrderMapper.updateOrderStatus(orderNo, status);
        return new Message(ResponseCodeType.SUCCESS, "更新成功", false);
    }

    @Override
    public Message getAllOrderDetail(String userId, int page, int size, String filter, String search) {

        if (!StringUtils.isBlank(search)) {
            search = "%" + search + "%";
        }
        // 分页查询订单表 获取 订单id
        PageHelper.startPage(page, size);
        List<OmsOrder> omsOrderListResult = omsOrderMapper.selectByUserIdAndStatusAndSkuName(userId, filter, search);

        String[] orderIds = omsOrderListResult.stream().map(OmsOrder::getId).toArray(String[]::new);

        if (orderIds.length == 0) {
            return new Message(ResponseCodeType.SUCCESS, null, true);
        }

        // 根据所有订单id获取item 根据订单创建时间排序
        List<OmsOrder> omsOrderList = omsOrderMapper.selectContainOrderItem(orderIds);
        int count = omsOrderMapper.selectByUserIdAndStatusAndSkuName(userId, filter, search).size();

        // 根据创建时间排序订单 设置所有订单的超时时间
        omsOrderList = omsOrderList.stream()
                .sorted((x, y) -> -(x.getCreateTime().getTime() + "").compareTo(y.getCreateTime().getTime() + "")).collect(Collectors.toList());

        omsOrderList.forEach(x -> x.setExpireTime(computedOrderExpireTime(x.getCreateTime())));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("omsOrderList", omsOrderList);
        jsonObject.put("count", count);
        return new Message(ResponseCodeType.SUCCESS, jsonObject, true);
    }

    @Override
    public OmsOrder selectByUserIdAndOrderNo(String userId, String orderNo) {
        OmsOrder omsOrderQuery = OmsOrder.builder().orderSn(orderNo).memberId(userId).build();
        return omsOrderMapper.selectOne(omsOrderQuery);
    }

    @Override
    public Message deleteOrder(String userId, String orderId) {
        OmsOrder omsOrderQuery = OmsOrder.builder().id(orderId).memberId(userId).build();
        omsOrderMapper.delete(omsOrderQuery);

        OmsOrderItem omsOrderItemQuery = OmsOrderItem.builder().orderId(orderId).build();
        omsOrderItemMapper.delete(omsOrderItemQuery);

        return new Message(ResponseCodeType.SUCCESS, "删除成功", true);
    }

    /**
     * 计算订单离超时还有多少毫秒
     * @param createTime  订单创建时间
     * @return 剩余多少毫秒
     */
    private Long computedOrderExpireTime(Date createTime) {
        Date date = new Date();
        Long time = createTime.getTime() + new Long(this.orderExpireTime) - date.getTime();
        if (time < 0) {
            return -1L;
        }
        return time;
    }

    private boolean hasRepository(List<OmsCartItem> omsCartItemList) {
        for (OmsCartItem omsCartItem : omsCartItemList) {
            PmsSkuRepository pmsSkuRepositoryQuery = PmsSkuRepository.builder()
                    .skuId(omsCartItem.getProductSkuId())
                    .build();
            PmsSkuRepository pmsSkuRepositoryResult = pmsSkuRepositoryMapper.selectOne(pmsSkuRepositoryQuery);
            if (pmsSkuRepositoryResult.getRepositoryTotal() < omsCartItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    private void lockRepository(List<OmsCartItem> omsCartItemList) {
        for (OmsCartItem omsCartItem : omsCartItemList) {
            omsCartItemMapper.decrByRepository(omsCartItem.getProductSkuId(), omsCartItem.getQuantity());
        }
    }

    /**
     * 生成订单编号  8位时间 + 6位以上 redis 自增值
     * @return
     */
    private String generateOrderSn() {
        StringBuffer stringBuffer = new StringBuffer();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = dateFormat.format(date);
        Long increment = stringRedisTemplate.opsForValue().increment(orderNoId + date);

        stringBuffer.append(format);
        if (increment.toString().length() < 6) {
            stringBuffer.append(String.format("%06d", increment));
        } else {
            stringBuffer.append(increment);
        }
        return stringBuffer.toString();
    }

    public BigDecimal computeTotalMoney(List<OmsCartItem> omsCartItemList) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItemList) {
            bigDecimal = bigDecimal.add(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
        }

        return bigDecimal;
    }
}
