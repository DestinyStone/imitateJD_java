package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsBaseCatalog3;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsBaseCatalog3Mapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsBaseCatalog3Service;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/11 16:25
 * @Description:
 */
@Service
public class PmsBaseCatalog3ServiceImpl implements PmsBaseCatalog3Service {

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog3> selectByCatalog2Id(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3Query = PmsBaseCatalog3.builder().catalog2Id(catalog2Id).build();
        List<PmsBaseCatalog3> pmsBaseCatalog3List = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3Query);
        return pmsBaseCatalog3List;
    }

    @Override
    public void updateById(String catalog1Id, String name) {
        PmsBaseCatalog3 pmsBaseCatalog3Query = PmsBaseCatalog3.builder().id(catalog1Id).name(name).build();
        pmsBaseCatalog3Mapper.updateByPrimaryKeySelective(pmsBaseCatalog3Query);
    }

    @Override
    public void deleteById(String catalog3Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = PmsBaseCatalog3.builder().id(catalog3Id).build();
        pmsBaseCatalog3Mapper.delete(pmsBaseCatalog3);
    }

    @Override
    public Message insertByNotNameRepetition(String name, String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3Query = PmsBaseCatalog3.builder().name(name).catalog2Id(catalog2Id).build();
        PmsBaseCatalog3 pmsBaseCatalog3Result = pmsBaseCatalog3Mapper.selectOne(pmsBaseCatalog3Query);
        if (pmsBaseCatalog3Result != null)
            return new Message(ResponseCodeType.PARAM_ERROR, "名字重复", false);
        pmsBaseCatalog3Mapper.insert(pmsBaseCatalog3Query);
        return new Message(ResponseCodeType.SUCCESS, "", true);
    }
}
