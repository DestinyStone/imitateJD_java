package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.*;
import com.imitatejd.maven.imitatejd_item_service.mapper.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsSkuInfoService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/21 12:19
 * @Description:
 */
@Service
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    private PmsSkuRepositoryMapper pmsSkuRepositoryMapper;

    @Override
    @Transactional
    public Message insert(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfo.setStatus(0);
        pmsSkuInfoMapper.insert(pmsSkuInfo);

        List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuInfo.getPmsSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        List<PmsSkuImage> pmsSkuImageList = pmsSkuInfo.getPmsSkuImageList();
        for (PmsSkuImage pmsSkuImage : pmsSkuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        PmsSkuRepository pmsSkuRepositoryQuery = PmsSkuRepository.builder().skuId(pmsSkuInfo.getId()).build();
        pmsSkuRepositoryQuery.setRepositoryTotal(0);
        pmsSkuRepositoryMapper.insert(pmsSkuRepositoryQuery);

        return new Message(ResponseCodeType.SUCCESS, "保存成功", true);
    }

    @Override
    public Message selectAllByProductIds(String[] pmsProductIds) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectBySpuIds(pmsProductIds);
//        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();
//        for (String pmsProductId  : pmsProductIds) {
//            PmsSkuInfo pmsSkuInfoQuery = PmsSkuInfo.builder().spuId(pmsProductId).build();
//            List<PmsSkuInfo> pmsSkuInfoResultList = pmsSkuInfoMapper.select(pmsSkuInfoQuery);
//
//            for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoResultList) {
//
//                PmsProductInfo pmsProductInfoQuery = PmsProductInfo.builder().id(pmsSkuInfo.getSpuId()).build();
//                PmsProductInfo pmsProductInfoResult = pmsProductInfoMapper.selectOne(pmsProductInfoQuery);
//                pmsSkuInfo.setSpuName(pmsProductInfoResult.getSpuName());
//
//                PmsSkuImage pmsSkuImageQuery = PmsSkuImage.builder().skuId(pmsSkuInfo.getId()).build();
//                List<PmsSkuImage> pmsSkuImageResultList = pmsSkuImageMapper.select(pmsSkuImageQuery);
//
//                for (PmsSkuImage pmsSkuImage : pmsSkuImageResultList) {
//                    PmsProductImage pmsProductImageQuery = PmsProductImage.builder().id(pmsSkuImage.getSpuImgId()).build();
//                    PmsProductImage pmsProductImageResult = pmsProductImageMapper.selectOne(pmsProductImageQuery);
//                    pmsSkuImage.setImgUrl(pmsProductImageResult.getImgUrl());
//                }
//                pmsSkuInfo.setPmsSkuImageList(pmsSkuImageResultList);
//
//                PmsSkuAttrValue pmsSkuAttrValueQuery = PmsSkuAttrValue.builder().skuId(pmsSkuInfo.getId()).build();
//                List<PmsSkuAttrValue> pmsSkuAttrValueResultList = pmsSkuAttrValueMapper.select(pmsSkuAttrValueQuery);
//                for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValueResultList) {
//                    PmsBaseAttrInfo pmsBaseAttrInfoQuery = PmsBaseAttrInfo.builder().id(pmsSkuAttrValue.getAttrId()).build();
//                    PmsBaseAttrInfo pmsBaseAttrInfoResult = pmsBaseAttrInfoMapper.selectOne(pmsBaseAttrInfoQuery);
//                    pmsSkuAttrValue.setAttrName(pmsBaseAttrInfoResult.getAttrName());
//
//                    PmsBaseAttrValue pmsBaseAttrValueQuery = PmsBaseAttrValue.builder().id(pmsSkuAttrValue.getValueId()).build();
//
//                    // TODO
//                    PmsBaseAttrValue pmsBaseAttrValueResult = pmsBaseAttrValueMapper.selectOne(pmsBaseAttrValueQuery);
//                    pmsSkuAttrValue.setValueName(pmsBaseAttrValueResult.getValueName());
//                }
//                pmsSkuInfo.setPmsSkuAttrValueList(pmsSkuAttrValueResultList);
//
//                PmsSkuRepository pmsSkuRepositoryQuery = PmsSkuRepository.builder().skuId(pmsSkuInfo.getId()).build();
//                PmsSkuRepository pmsSkuRepository = pmsSkuRepositoryMapper.selectOne(pmsSkuRepositoryQuery);
//                pmsSkuInfo.setRepositoryTotal(pmsSkuRepository.getRepositoryTotal());
//            }
//            pmsSkuInfoList.addAll(pmsSkuInfoResultList);
//        }
//        System.out.println(pmsSkuInfoList);
        return new Message(ResponseCodeType.SUCCESS, pmsSkuInfoList, true);
    }

    @Override
    public Message updateStatusById(String skuId) {
        try {
            PmsSkuInfo pmsSkuInfoQuery = PmsSkuInfo.builder().id(skuId).status(1).build();
            pmsSkuInfoMapper.updateByPrimaryKeySelective(pmsSkuInfoQuery);
            return new Message(ResponseCodeType.SUCCESS, null, true);
        }catch (Exception e) {

        }  return new Message(ResponseCodeType.NO_LOGIN_STATUS, null, false);


    }

    @Override
    public Message selectById(String id) {
        PmsSkuInfo pmsSkuInfoQuery = PmsSkuInfo.builder().id(id).status(1).build();
        PmsSkuInfo pmsSkuInfoResult = pmsSkuInfoMapper.selectOne(pmsSkuInfoQuery);

        if (pmsSkuInfoResult == null) {
            return new Message(ResponseCodeType.NO_COMMODITY, "商品未销售", true);
        }

        List<PmsSkuImage> pmsSkuImageList = pmsSkuImageMapper.selectImageBySkuId(id);
        pmsSkuInfoResult.setPmsSkuImageList(pmsSkuImageList);
        return new Message(ResponseCodeType.SUCCESS, pmsSkuInfoResult, true);
    }

    @Override
    public Message selectAllSkuAttrListById(String id) {
        PmsSkuInfo pmsSkuInfoIdQuery = PmsSkuInfo.builder().id(id).build();
        PmsSkuInfo pmsSkuInfoResult = pmsSkuInfoMapper.selectOne(pmsSkuInfoIdQuery);
        PmsSkuInfo pmsSkuInfoSpuIdQuery = PmsSkuInfo.builder().spuId(pmsSkuInfoResult.getSpuId()).build();
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.select(pmsSkuInfoSpuIdQuery);
        String[] ids = pmsSkuInfoList.stream().map(PmsSkuInfo::getId).toArray(String[]::new);
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.selectBySkuIds(ids, id);

        // 对二级属性进行排序
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfoList) {
            List<PmsBaseAttrValue> pmsBaseAttrValueListSortResult = pmsBaseAttrInfo.getPmsBaseAttrValueList().stream().sorted(Comparator.comparing(PmsBaseAttrValue::getId)).collect(Collectors.toList());
            pmsBaseAttrInfo.setPmsBaseAttrValueList(pmsBaseAttrValueListSortResult);
        }

        return new Message(ResponseCodeType.SUCCESS, pmsBaseAttrInfoList, true);
    }

    @Override
    public Integer selectValueIds(String spuId, String[] valueIds) {
        Integer i = pmsSkuInfoMapper.selectByValueIds(spuId, valueIds);
        return i;
    }

    @Override
    public Message selectByIds(String[] skuIds) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectByIds(skuIds);
        return new Message(ResponseCodeType.SUCCESS, pmsSkuInfoList, true );
    }

    @Override
    public List<PmsSkuInfo> selectAll() {
        // 找到默认图片所有spu的默认商品
        return pmsSkuInfoMapper.selectAllByDefaultImage();
    }
}
