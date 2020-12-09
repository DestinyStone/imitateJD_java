package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsProductReject;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsProductRejectMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsProductRejectService;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/14 16:59
 * @Description:
 */
@Service
public class PmsProductRejectServiceImpl implements PmsProductRejectService {

    @Autowired
    private PmsProductRejectMapper pmsProductRejectMapper;

    @Override
    public Message selectBySpuId(String spuId) {
        PmsProductReject pmsProductRejectQuery = PmsProductReject.builder().productId(spuId).build();
        PmsProductReject pmsProductReject = pmsProductRejectMapper.selectOne(pmsProductRejectQuery);
        return new Message(ResponseCodeType.SUCCESS, pmsProductReject, true);
    }
}
