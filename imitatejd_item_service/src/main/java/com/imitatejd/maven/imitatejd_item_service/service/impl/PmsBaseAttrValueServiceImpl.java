package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsBaseAttrValue;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsBaseAttrValueMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsBaseAttrValueService;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/13 15:08
 * @Description:
 */
@Service
public class PmsBaseAttrValueServiceImpl implements PmsBaseAttrValueService {

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public Message selectByAttrId(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValueQuery = PmsBaseAttrValue.builder().attrId(attrId).build();
        List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValueQuery);
        return new Message(ResponseCodeType.SUCCESS, pmsBaseAttrValueList, true);
    }

    @Override
    public Message deleteById(String id) {
        PmsBaseAttrValue pmsBaseAttrValueQuery = PmsBaseAttrValue.builder().id(id).build();
        pmsBaseAttrValueMapper.deleteByPrimaryKey(pmsBaseAttrValueQuery);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @Override
    public Message insertByNotNameRepetition(String attrId, String valueName) {
        PmsBaseAttrValue pmsBaseAttrValueQuery = PmsBaseAttrValue.builder().attrId(attrId).valueName(valueName).build();
        PmsBaseAttrValue pmsBaseAttrValuetResult = pmsBaseAttrValueMapper.selectOne(pmsBaseAttrValueQuery);
        if (pmsBaseAttrValuetResult != null)
            return new Message(ResponseCodeType.PARAM_ERROR, "名字重复", false);
        pmsBaseAttrValueMapper.insert(pmsBaseAttrValueQuery);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @Override
    public Message updateByNotNameRepetition(String id, String valueName) {
        PmsBaseAttrValue pmsBaseAttrValueQuery = PmsBaseAttrValue.builder().id(id).valueName(valueName).build();
        pmsBaseAttrValueMapper.updateByPrimaryKeySelective(pmsBaseAttrValueQuery);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }
}
