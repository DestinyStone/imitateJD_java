package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsBaseCatalog2;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsBaseCatalog2Mapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsBaseCatalog2Service;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/11 16:25
 * @Description:
 */
@Service
public class PmsBaseCatalog2ServiceImpl implements PmsBaseCatalog2Service {

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Override
    public List<PmsBaseCatalog2> selectByCatalog1Id(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2Query = PmsBaseCatalog2.builder().catalog1Id(catalog1Id).build();
        List<PmsBaseCatalog2> pmsBaseCatalog2List = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2Query);
        return pmsBaseCatalog2List;
    }

    @Override
    public void updateById(String catalog1Id, String name) {
        PmsBaseCatalog2 pmsBaseCatalog2Query = PmsBaseCatalog2.builder().id(catalog1Id).name(name).build();
        pmsBaseCatalog2Mapper.updateByPrimaryKeySelective(pmsBaseCatalog2Query);
    }

    @Override
    public void deleteById(String catalog2Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = PmsBaseCatalog2.builder().id(catalog2Id).build();
        pmsBaseCatalog2Mapper.delete(pmsBaseCatalog2);
    }

    @Override
    public Message insertByNotNameRepetition(String name, String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2Query = PmsBaseCatalog2.builder().name(name).catalog1Id(catalog1Id).build();
        PmsBaseCatalog2 pmsBaseCatalog2Result = pmsBaseCatalog2Mapper.selectOne(pmsBaseCatalog2Query);
        if (pmsBaseCatalog2Result != null)
            return new Message(ResponseCodeType.PARAM_ERROR, "名字重复", false);
        pmsBaseCatalog2Query.setCatalog1Id(catalog1Id);
        return new Message(ResponseCodeType.SUCCESS, "", true);
    }

}
