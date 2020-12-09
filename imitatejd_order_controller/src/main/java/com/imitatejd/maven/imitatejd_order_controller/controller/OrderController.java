package com.imitatejd.maven.imitatejd_order_controller.controller;

import bean.OmsOrder;
import bean.UmsMember;
import com.imitatejd.maven.imitatejd_order_controller.annon.VisitAstrice;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
import service.OmsOrderService;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/30 13:14
 * @Description:
 */
@RestController
@CrossOrigin
public class OrderController {

    @Reference
    private OmsOrderService omsOrderService;

    @GetMapping("/submitOrder")
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @VisitAstrice
    @ApiOperation("提交订单")
    public Message submitOrder(@RequestParam("skuIds") String[] skuIds, @RequestParam("addressId") String addressId) {

       Subject subject = SecurityUtils.getSubject();
       final String userId = ((UmsMember) subject.getPrincipal()).getId();
       return omsOrderService.genOrder(userId, skuIds, addressId);
    }

    @GetMapping(value = "/getOrder", params = {"orderNo!="})
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @RequiresAuthentication
    @ApiOperation("根据订单号获取订单")
    public Message getOrder(@RequestParam("orderNo") String orderNo) {
        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();
        return omsOrderService.getOrderDetail(orderNo, userId);
    }

    @GetMapping (value = "/getAllOrderDetail")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token", required = true)})
    @RequiresAuthentication
    @ApiOperation("获取订单详情")
    public Message getAllOrderDetail(@RequestParam("page") int page,
                                     @RequestParam("size") int size,
                                     @RequestParam(value = "filter", required = false) String filter,
                                     @RequestParam(value = "search", required = false) String search) {
        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();
        return omsOrderService.getAllOrderDetail(userId, page, size, filter, search);
    }

    @GetMapping(value = "/cancelOrder")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", dataType = "String", name = "token", required = true)})
    @RequiresAuthentication
    @ApiOperation("根据订单号取消订单")
    public Message cancelOrder(@RequestParam("orderNo") String orderNo) {

        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();

        // 检测订单号是否属于该用户的
        OmsOrder omsOrder = omsOrderService.selectByUserIdAndOrderNo(userId, orderNo);
        if (omsOrder == null) {
            return new Message(ResponseCodeType.PARAM_ERROR, "订单号异常", false);
        }
        return omsOrderService.updateOrderStatus(orderNo, 4);
    }

    @GetMapping(value = "/deleteOrder")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", dataType = "String", name = "token", required = true)})
    @RequiresAuthentication
    @ApiOperation("根据订单号删除订单")
    public Message deleteOrder(@RequestParam("orderNo") String orderNo) {

        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();

        // 检测订单号是否属于该用户的
        OmsOrder omsOrder = omsOrderService.selectByUserIdAndOrderNo(userId, orderNo);
        if (omsOrder == null) {
            return new Message(ResponseCodeType.PARAM_ERROR, "订单号异常", false);
        }

        // 订单状态为 0 和 5 才可删除
        if (omsOrder.getStatus() != 0 && omsOrder.getStatus() != 5) {
            return new Message(ResponseCodeType.PARAM_ERROR, "订单不可删除", false);
        }

        return omsOrderService.deleteOrder(userId, omsOrder.getId());
    }
}
