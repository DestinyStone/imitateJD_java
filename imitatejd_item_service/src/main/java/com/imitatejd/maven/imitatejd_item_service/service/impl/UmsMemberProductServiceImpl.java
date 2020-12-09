package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.UmsMemberProduct;
import com.imitatejd.maven.imitatejd_item_service.mapper.UmsMemberProductMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import service.UmsMemberProductService;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/20 12:31
 * @Description:
 */
@Service
public class UmsMemberProductServiceImpl implements UmsMemberProductService {

    @Autowired
    private UmsMemberProductMapper umsMemberProductMapper;

    @Override
    public List<UmsMemberProduct> getUemberProductAllByUemId(String id) {
        UmsMemberProduct umsMemberProductQuery = UmsMemberProduct.builder().umsId(id).build();
        List<UmsMemberProduct> umsMemberProductList = umsMemberProductMapper.select(umsMemberProductQuery);
        return umsMemberProductList;
    }
}
