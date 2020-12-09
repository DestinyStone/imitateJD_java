package com.imitatejd.maven.imitatejd_search_controller;


import bean.PmsSkuInfo;
import com.alibaba.fastjson.JSONObject;
import com.imitatejd.maven.imitatejd_search_controller.repository.PmsSkuInfoRepository;
import document.PmsSkuInfoSearch;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ImitatejdSearchControllerApplicationTests {
//    @Reference
//    private PmsSkuInfoService pmsSkuInfoService;
//
//    @Reference
//    private PmsProductInfoService pmsProductInfoService;
//
//    @Reference
//    private PmsProductImageService pmsProductImageService;
//
    @Autowired
    private PmsSkuInfoRepository pmsSkuInfoRepository;
//
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
//
//    @Test
//    void createIndex() {
//        elasticsearchTemplate.createIndex(PmsSkuInfoSearch.class);
//        elasticsearchTemplate.putMapping(PmsSkuInfoSearch.class);
//    }
//
    @Test
    void test() {
        elasticsearchTemplate.createIndex(PmsSkuInfoSearch.class);
        elasticsearchTemplate.putMapping(PmsSkuInfoSearch.class);
    }

    @Test
    void contextLoads() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("D://1.txt");
        byte[] buff = new byte[fileInputStream.available()];
        fileInputStream.read(buff);
        List<PmsSkuInfo> pmsSkuInfoList = JSONObject.parseArray(new String(buff), PmsSkuInfo.class);

        System.out.println(pmsSkuInfoList.size());
        List<PmsSkuInfoSearch> pmsSkuInfoSearches = new ArrayList<>();
        pmsSkuInfoList.forEach(pmsSkuInfo -> {
            PmsSkuInfoSearch pmsSkuInfoSearch = new PmsSkuInfoSearch();
            try {
                BeanUtils.copyProperties(pmsSkuInfoSearch, pmsSkuInfo);
                pmsSkuInfoSearches.add(pmsSkuInfoSearch);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        pmsSkuInfoRepository.saveAll(pmsSkuInfoSearches);
    }

}
