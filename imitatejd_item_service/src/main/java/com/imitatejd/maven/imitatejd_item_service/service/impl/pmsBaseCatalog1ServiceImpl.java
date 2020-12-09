package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsBaseCatalog1;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsBaseCatalog1Mapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsBaseCatalog1Service;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/11 16:09
 * @Description:
 */
@Service
public class pmsBaseCatalog1ServiceImpl implements PmsBaseCatalog1Service {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Override
    public List<PmsBaseCatalog1> selectAll() {
        List<PmsBaseCatalog1> pmsBaseCatalog1List = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1List;
    }

    @Override
    public void updateById(String catalog1Id, String name) {
        PmsBaseCatalog1 pmsBaseCatalog1Query = PmsBaseCatalog1.builder().id(catalog1Id).name(name).build();
        pmsBaseCatalog1Mapper.updateByPrimaryKeySelective(pmsBaseCatalog1Query);
    }

    @Override
    public void deleteById(String catalog1Id) {
        PmsBaseCatalog1 pmsBaseCatalog1 = PmsBaseCatalog1.builder().id(catalog1Id).build();
        pmsBaseCatalog1Mapper.delete(pmsBaseCatalog1);
    }

    @Override
    public Message insertByNotNameRepetition(String name) {
        PmsBaseCatalog1 pmsBaseCatalog1Query = PmsBaseCatalog1.builder().name(name).build();
        PmsBaseCatalog1 pmsBaseCatalog11Result = pmsBaseCatalog1Mapper.selectOne(pmsBaseCatalog1Query);
        if (pmsBaseCatalog11Result != null)
            return new Message(ResponseCodeType.PARAM_ERROR, "名字重复", false);
        pmsBaseCatalog1Mapper.insert(pmsBaseCatalog1Query);
        return new Message(ResponseCodeType.SUCCESS, "", true);
    }

}
