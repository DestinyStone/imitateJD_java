package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsBaseAttrInfo;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsBaseAttrInfoMapper;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsBaseAttrValueMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsBaseAttrInfoService;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/12 10:17
 * @Description:
 */
@Service
public class PmsBaseAttrInfoServiceImpl implements PmsBaseAttrInfoService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getPmsBaseAttrInfoByCatalog3Id(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfoQuery = PmsBaseAttrInfo.builder().catalog3Id(catalog3Id).build();
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfoQuery);
        return pmsBaseAttrInfoList;
    }

    @Override
    public Message addPmsBaseAttrInfoByCatalog3Id(String name, String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfoQuery = PmsBaseAttrInfo.builder().attrName(name).catalog3Id(catalog3Id).build();
        PmsBaseAttrInfo pmsBaseAttrInfoResult = pmsBaseAttrInfoMapper.selectOne(pmsBaseAttrInfoQuery);
        if (pmsBaseAttrInfoResult != null)
            return new Message(ResponseCodeType.PARAM_ERROR, "名字重复", false);

        pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfoQuery);
        return new Message(ResponseCodeType.SUCCESS, "", true);
    }


    @Override
    public Message updatePmsBaseAttrInfo(String id, String name) {
        PmsBaseAttrInfo pmsBaseAttrInfoQuery = PmsBaseAttrInfo.builder().id(id).build();
        pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfoQuery);
        return  new Message(ResponseCodeType.SUCCESS, "", true);
    }

    @Override
    public Message deletePmsBaseAttrInfo(String id) {
        PmsBaseAttrInfo pmsBaseAttrInfoQuery = PmsBaseAttrInfo.builder().id(id).build();
        pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfoQuery);
        return new Message(ResponseCodeType.SUCCESS, "", true);
    }
}
