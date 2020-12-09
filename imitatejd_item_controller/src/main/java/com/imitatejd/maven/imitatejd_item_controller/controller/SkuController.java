package com.imitatejd.maven.imitatejd_item_controller.controller;

import bean.PmsSkuInfo;
import bean.UmsMemberProduct;
import com.alibaba.fastjson.JSONObject;
import com.imitatejd.maven.imitatejd_item_controller.annon.BasicEnableCache;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsSkuInfoService;
import service.PmsSkuRepositoryService;
import service.UmsMemberProductService;
import service.UmsMemberService;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/21 12:13
 * @Description:
 */
@RestController
@CrossOrigin
public class SkuController {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private UmsMemberService umsMemberService;

    @Reference
    private UmsMemberProductService umsMemberProductService;

    @Reference
    private PmsSkuRepositoryService pmsSkuRepositoryService;

    @PostMapping("/addPmsSkuInfo")
    @ApiOperation("添加商品")
    public Message addPmsSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        return pmsSkuInfoService.insert(pmsSkuInfo);
    }

    @GetMapping(value = "/getPmsSkuInfoById", params = {"id!="})
    @BasicEnableCache(tableName = "pms_sku_info", idName = "id")
    @ApiOperation("根据skuId查询商品")
    public Message  getPmsSkuInfoById(@RequestParam("id") String id) {
        return pmsSkuInfoService.selectById(id);
    }

    @GetMapping(value="/getPmsSkuAttrListBySkuId", params = {"id!="})
    @BasicEnableCache(tableName = "pms_sku_info_attr", idName = "id")
    @ApiOperation("查询当前系列商品下的所有销售属性")
    public Message getPmsSkuAttrListBySkuId(@RequestParam("id") String id) {
        return pmsSkuInfoService.selectAllSkuAttrListById(id);
    }

    @GetMapping("/getUmsMemberPmsSkuInfoAll")
    @ApiOperation("获取店家用户出售的所有商品")
    public Message getUmsMemberPmsSkuInfoAll(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String id = umsMemberService.selectByUsernameReturnId(username);

        List<UmsMemberProduct> umsMemberProductList =  umsMemberProductService.getUemberProductAllByUemId(id);
        if (umsMemberProductList == null || umsMemberProductList.size() == 0) {
            return new Message(ResponseCodeType.SUCCESS, null, true);
        }
        String[] pmsProductIds = umsMemberProductList.stream().map(umsMemberProduct -> umsMemberProduct.getProductId()).toArray(String[]::new);
        return pmsSkuInfoService.selectAllByProductIds(pmsProductIds);
    }

    @PostMapping(value = "/sellPmsSkuInfo", params = {"skuId!=", "repositoryTotal!="})
    @ApiOperation("更新商品初始库存")
    public Message sellPmsSkuInfo(String skuId, String repositoryTotal) {
        Message message = pmsSkuRepositoryService.updateRepositoryTotalBySkuId(skuId, repositoryTotal);
        if (message.getStatus() == false)
            return message;
        return pmsSkuInfoService.updateStatusById(skuId);
    }

    @PostMapping(value = "/getPmsSkuInfoByValueIds")
    @ApiOperation("根据销售属性查找商品")
    public Message getPmsSkuInfoByValueId(String spuId, String[] valueIds) {
        Integer skuId = pmsSkuInfoService.selectValueIds(spuId, valueIds);
        if (skuId == null) {
            return new Message(ResponseCodeType.NO_COMMODITY, null, true);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("skuId", skuId);

        return new Message(ResponseCodeType.SUCCESS, jsonObject, true);
    }

    @PostMapping("/getPmsSkuInfoBySkuIds")
    @ApiOperation("查找多个sku商品")
    public Message getPmsSkuInfoBySkuIds(@RequestBody String[] skuIds) {
        if (skuIds.length == 0)
            return new Message(ResponseCodeType.SUCCESS, null, true);
        return pmsSkuInfoService.selectByIds(skuIds);
    }
}
