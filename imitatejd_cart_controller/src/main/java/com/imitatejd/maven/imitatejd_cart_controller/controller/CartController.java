package com.imitatejd.maven.imitatejd_cart_controller.controller;

import bean.OmsCartItem;
import bean.PmsSkuInfo;
import bean.UmsMember;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import response.Message;
import response.type.ResponseCodeType;
import service.OmsCartItemService;
import service.PmsSkuInfoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/31 08:55
        * @Description:
        */
@RestController
@CrossOrigin
public class CartController {

    @Reference
    private OmsCartItemService omsCartItemService;

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @PostMapping("/addCart")
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @ApiOperation("添加商品到购物车")
    public Message addCart(@RequestBody List<OmsCartItem> cartItemList) {

        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();

        final List<OmsCartItem> cartItemListQuery = cartItemList.stream()
                .filter(cartItem -> !StringUtils.isBlank(cartItem.getProductSkuId()) && cartItem.getQuantity() > 0)
                .map(cartItem -> {
                    return OmsCartItem.builder().productSkuId(cartItem.getProductSkuId()).quantity(cartItem.getQuantity()).build();
                }).collect(Collectors.toList());
        Message message = pmsSkuInfoService.selectByIds(cartItemListQuery.stream().map(x -> x.getProductSkuId()).toArray(String[]::new));
        List<PmsSkuInfo> pmsSkuInfoList = (List<PmsSkuInfo>)message.getResponseMessage();
        cartItemListQuery.stream().forEach(cartItem -> {
            cartItem.setMemberId(userId);
            Optional<PmsSkuInfo> first = pmsSkuInfoList.stream().filter(x -> cartItem.getProductSkuId().equals(x.getId())).findFirst();
            cartItem.setPrice(first.get().getPrice());
        });

        return omsCartItemService.insertBySkuIdAndQuantity(cartItemListQuery);
    }

    @GetMapping("/getCart")
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @ApiOperation("获取购物车")
    public Message getCart() {
        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();

        return omsCartItemService.selectByUserId(userId);
    }

    @GetMapping("updateCartSkuTotal")
    @RequiresAuthentication
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", dataType = "String", name = "token", required = true)})
    @ApiOperation("更新购物车商品数量")
    public Message updateCartSkuTotal(@RequestParam("skuId") String skuId, @RequestParam("cartSkuTotal") Integer skuTotal) {
        if (skuTotal == null || skuTotal < 1 || skuTotal > 9999) {
            return new Message(ResponseCodeType.PARAM_ERROR, "参数错误", false);
        }

        Subject subject = SecurityUtils.getSubject();
        final String userId = ((UmsMember) subject.getPrincipal()).getId();

        return omsCartItemService.updateCartTotal(userId, skuId, skuTotal);
    }
}

