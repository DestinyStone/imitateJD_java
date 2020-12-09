package com.imitatejd.maven.imitatejd_order_controller.controller;

import bean.OmsOrder;
import bean.UmsMember;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.Message;
import response.type.ResponseCodeType;
import service.AlipayService;
import service.OmsOrderService;

import java.util.HashMap;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 22:55
 * @Description:
 */
@RestController
@CrossOrigin
public class PaymentController {

    @Reference
    private AlipayService alipayService;

    @Reference
    private OmsOrderService omsOrderService;

    @GetMapping(value = "/getAlipayQrcode", params = {"orderNo!="})
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @RequiresAuthentication
    @ApiOperation("获取支付二维码")
    public Message getAlipayQrcode(@RequestParam("orderNo") String orderNo) {

        Subject subject = SecurityUtils.getSubject();
        String userId = ((UmsMember) subject.getPrincipal()).getId();
        Message orderDetail = omsOrderService.getOrderDetail(orderNo, userId);
        HashMap<String, Object> result = (HashMap) orderDetail.getResponseMessage();

        OmsOrder omsOrder = (OmsOrder)result.get("omsOrder");

        // 只有订单未支付才会创建二维码
        if (omsOrder.getStatus() != 0) {
            return new Message(ResponseCodeType.SUCCESS, "订单以支付", true);
        }

        String qrcodeBase64 = alipayService.createQrcode(omsOrder.getOrderSn(), omsOrder.getTotalAmount());
        if (StringUtils.isBlank(qrcodeBase64)) {
            return new Message(ResponseCodeType.UN_KNOW_ERROR, null, false);
        }
        return new Message(ResponseCodeType.SUCCESS, qrcodeBase64, true);
    }

    @GetMapping(value="/queryOrderAlipayStatus", params = {"orderNo!="})
    @RequiresAuthentication
    @ApiImplicitParams(@ApiImplicitParam(name="token", value = "token", paramType = "header", dataType = "String", required = true))
    @ApiOperation("获取支付状态")
    public Message queryOrderStatus(@RequestParam("orderNo") String orderNo) {
        Message message = alipayService.queryOrderAlipayStatus(orderNo);
        if (ResponseCodeType.TRADE_SUCCESS.equals(message.getResponseCode())) {
            // 修改订单状态为已支付
            omsOrderService.updateOrderStatus(orderNo, 1);
        }
        return message;
    }
}
