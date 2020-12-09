package com.imitatejd.maven.imitatejd_search_controller.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsSkuInfoSearchService;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/10 19:58
 * @Description:
 */
@RestController
@CrossOrigin
public class SearchController {

    @Reference
    private PmsSkuInfoSearchService pmsSkuInfoSearchService;

    @GetMapping("/searchSkuInfoList")
    @ApiOperation("搜索sku")
    public Message searchSkuInfoList(@RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "valueIds", required = false) String[] valueIds,
                                    @RequestParam(value = "catalog3Id", required = false) String catalog3Id,
                                     @RequestParam(value = "currentPage") String currentPage,
                                     @RequestParam(value = "size") String size,
                                     @RequestParam(value = "priceGte", required = false) String priceGte,
                                     @RequestParam(value = "priceLte", required = false) String priceLte,
                                     @RequestParam(value = "sort", required = false) String sort) {
        if (StringUtils.isBlank(keyword) && StringUtils.isBlank(catalog3Id) && valueIds == null) {
            return new Message(ResponseCodeType.PARAM_ERROR, null, false);
        }
        return pmsSkuInfoSearchService.selectByCondition(keyword, valueIds, catalog3Id, currentPage, size, priceGte, priceLte, sort);
    }
}
