package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.*;
import bean.custom.SelectCheckEnum;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.imitatejd.maven.imitatejd_item_service.mapper.*;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsProductInfoService;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/13 22:43
 * @Description:
 */
@Service
public class PmsProductInfoServiceImpl implements PmsProductInfoService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    private PmsProductRejectMapper pmsProductRejectMapper;

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Autowired
    private UmsMemberProductMapper umsMemberProductMapper;

    @Autowired
    private PmsProductAttrMapper pmsProductAttrMapper;

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Override
    public Message updateStautsById(String id, Integer status) {
        PmsProductInfo pmsProductInfoQuery = PmsProductInfo.builder().id(id).status(status).build();
        pmsProductInfoMapper.updateByPrimaryKeySelective(pmsProductInfoQuery);

        if (status == 1) {
            PmsProductReject pmsProductReject = PmsProductReject.builder().productId(id).build();
            pmsProductRejectMapper.delete(pmsProductReject);
        }

        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @Override
    @Transactional
    public Message rejectPmsProductInfo(String id, String content) {

        PmsProductReject pmsProductRejectQuery = PmsProductReject.builder().productId(id).content(content).build();
        pmsProductRejectMapper.insertSelective(pmsProductRejectQuery);
        this.updateStautsById(id, 2);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @Override
    public Message selectByMultiConditions(String selectCheck, String sort, int page, int size, String name, String value) {
        // 构建查询条件
        Example example = new Example(PmsProductInfo.class);
        Example.Criteria criteria = example.createCriteria();

        this.selectCheckBuilder(criteria, selectCheck);
        this.searchBuilder(criteria, name, value);
        this.sortBuilder(example, sort);
        PageHelper.startPage(page, size, false);

        // 查询
        List<PmsProductInfo> pmsProductInfoList = pmsProductInfoMapper.selectByExample(example);
        //设置状态描述
        pmsProductInfoList.stream().forEach(x -> {
            if (x.getStatus() == 0) x.setStatesDescription("未通过");
            if (x.getStatus() == 1) x.setStatesDescription("已通过");
            if (x.getStatus() == 2) x.setStatesDescription("已驳回");
        });

        // 查询数据总量
        int total = pmsProductInfoMapper.selectCount(null);

        JSONObject responseMessage = new JSONObject();
        responseMessage.put("pmsProductInfoList", pmsProductInfoList);
        responseMessage.put("total", total);
        return new Message(ResponseCodeType.SUCCESS, responseMessage, true);
    }

    @Override
    public Message selectDetailById(String id) {
        PmsProductInfo pmsProductInfoQuery = PmsProductInfo.builder().id(id).build();
        PmsProductInfo pmsProductInfoResult = pmsProductInfoMapper.selectOne(pmsProductInfoQuery);

        this.setCatalogName(pmsProductInfoResult);

        PmsProductImage pmsProductImageQuery = PmsProductImage.builder().productId(id).build();
        List<PmsProductImage> pmsProductImageList = pmsProductImageMapper.select(pmsProductImageQuery);
        pmsProductInfoResult.setPmsProductImageList(pmsProductImageList);

        return new Message(ResponseCodeType.SUCCESS, pmsProductInfoResult, true);
    }

    @Override
    @Transactional
    public Message insert(String id, PmsProductInfo pmsProductInfo) {
        PmsProductInfo pmsProductInfoQueryName = PmsProductInfo.builder().spuName(pmsProductInfo.getSpuName()).build();

        PmsProductInfo pmsProductInfoResultName = pmsProductInfoMapper.selectOne(pmsProductInfoQueryName);
        if (pmsProductInfoResultName != null) {
            return new Message(ResponseCodeType.PARAM_ERROR, "商品名称重复", false);
        }
        try {
            pmsProductInfo.setCreateTime(new Date());
            pmsProductInfo.setStatus(0);
            pmsProductInfoMapper.insertSelective(pmsProductInfo);

            List<PmsProductImage> pmsProductImageList = pmsProductInfo.getPmsProductImageList();
            pmsProductImageList.forEach(pmsProductImage -> {
                pmsProductImage.setProductId(pmsProductInfo.getId());
                pmsProductImageMapper.insertSelective(pmsProductImage);
            });

            List<PmsProductAttr> pmsProductAttrListQuery = pmsProductInfo.getPmsProductAttrList();
            for (PmsProductAttr pmsProductAttr : pmsProductAttrListQuery) {
                pmsProductAttr.setProductId(pmsProductInfo.getId());
                pmsProductAttrMapper.insertSelective(pmsProductAttr);
            }

            UmsMemberProduct umsMenberProductQuery = UmsMemberProduct.builder().umsId(id).productId(pmsProductInfo.getId()).build();
            umsMemberProductMapper.insertSelective(umsMenberProductQuery);
            return new Message(ResponseCodeType.SUCCESS, "成功", true);
        }catch (Exception e) {
            e.printStackTrace();
            return new Message(ResponseCodeType.NO_LOGIN_STATUS, "失败", false);
        }
    }

    @Override
    public Message getUserProductInfoByUserId(String userId) {
        UmsMemberProduct umsMemberProductQuery = UmsMemberProduct.builder().umsId(userId).build();
        List<UmsMemberProduct> umsMemberProductList = umsMemberProductMapper.select(umsMemberProductQuery);
        List<PmsProductInfo> pmsProductInfoList = new ArrayList<>();
        umsMemberProductList.forEach(umsMemberProduct -> {
            PmsProductInfo pmsProductInfoQuery = PmsProductInfo.builder().id(umsMemberProduct.getProductId()).build();
            PmsProductInfo pmsProductInfoResult = pmsProductInfoMapper.selectOne(pmsProductInfoQuery);

            this.setCatalogName(pmsProductInfoResult);

            PmsProductImage pmsProductImageQuery = PmsProductImage.builder().productId(pmsProductInfoResult.getId()).build();
            List<PmsProductImage> pmsProductImageList = pmsProductImageMapper.select(pmsProductImageQuery);
            pmsProductInfoResult.setPmsProductImageList(pmsProductImageList);

            PmsProductAttr pmsProductAttrQuery = PmsProductAttr.builder().productId(umsMemberProduct.getProductId()).build();
            List<PmsProductAttr> pmsProductAttrList = pmsProductAttrMapper.select(pmsProductAttrQuery);
            for (PmsProductAttr pmsProductAttr : pmsProductAttrList) {
                PmsBaseAttrInfo pmsBaseAttrInfoQuery = PmsBaseAttrInfo.builder().id(pmsProductAttr.getAttrId()).build();
                PmsBaseAttrInfo pmsBaseAttrInfoResult = pmsBaseAttrInfoMapper.selectOne(pmsBaseAttrInfoQuery);
                pmsProductAttr.setAttrName(pmsBaseAttrInfoResult.getAttrName());
            }
            pmsProductInfoResult.setPmsProductAttrList(pmsProductAttrList);

            pmsProductInfoList.add(pmsProductInfoResult);
        });

        return new Message(ResponseCodeType.SUCCESS, pmsProductInfoList, true);
    }

    @Override
    @Transactional
    public Message updateById(PmsProductInfo pmsProductInfo) {
        PmsProductInfo pmsProductInfoQuery = PmsProductInfo.builder().spuName(pmsProductInfo.getSpuName()).build();
        PmsProductInfo pmsProductInfoResult = pmsProductInfoMapper.selectOne(pmsProductInfoQuery);
        if (pmsProductInfoResult != null && !pmsProductInfoResult.getId().equals(pmsProductInfo.getId())) {
            return new Message(ResponseCodeType.PARAM_ERROR, "名字重复", false);
        }
        pmsProductInfo.setCreateTime(new Date());
        pmsProductInfo.setStatus(0);
        pmsProductInfoMapper.updateByPrimaryKeySelective(pmsProductInfo);

        List<PmsProductImage> pmsProductImageList = pmsProductInfo.getPmsProductImageList();

        PmsProductImage pmsProductImageQuery = PmsProductImage.builder().productId(pmsProductInfo.getId()).build();
        List<PmsProductImage> pmsProductImageListResult = pmsProductImageMapper.select(pmsProductImageQuery);

        // 如果后端中的图片， 在前端中不存在则过滤出来
        pmsProductImageListResult.stream().filter(pmsProductImageResult -> {
            // 遍历前端的图片
            int i = 0;
            for (; i < pmsProductImageList.size(); i++) {
                if (pmsProductImageResult.getId().equals(pmsProductImageList.get(i).getId())) {
                    break;
                }
            }
            // 说明后端的这一张图片，在前端中不存在
            if (i == pmsProductImageList.size()) {
                pmsProductImageMapper.deleteByPrimaryKey(pmsProductImageResult);
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        pmsProductImageList.forEach(pmsProductImage -> {
            pmsProductImage.setProductId(pmsProductInfo.getId());
            if (StringUtils.isBlank(pmsProductImage.getId())) {
                pmsProductImageMapper.insertSelective(pmsProductImage);
            } else {
                pmsProductImageMapper.updateByPrimaryKeySelective(pmsProductImage);
            }
        });

        // 处理销售属性
        PmsProductAttr pmsProductAttrQuery = PmsProductAttr.builder().productId(pmsProductInfo.getId()).build();
        pmsProductAttrMapper.delete(pmsProductAttrQuery);

        List<PmsProductAttr> pmsProductAttrList = pmsProductInfo.getPmsProductAttrList();
        pmsProductAttrList.forEach(pmsProductAttr -> {
            pmsProductAttr.setProductId(pmsProductInfo.getId());
            pmsProductAttrMapper.insertSelective(pmsProductAttr);
        });


        PmsProductReject pmsProductRejectQuery = PmsProductReject.builder().productId(pmsProductInfo.getId()).build();
        pmsProductRejectMapper.delete(pmsProductRejectQuery);

        return new Message(ResponseCodeType.SUCCESS, "更新成功", true);
    }


    private void selectCheckBuilder(Example.Criteria criteria, String selectCheck) {
       if ((SelectCheckEnum.ALL + "").equals(selectCheck)) return;
       if ((SelectCheckEnum.STATUS0 + "").equals(selectCheck)) criteria.andEqualTo("status", 0);
       if ((SelectCheckEnum.STATUS1 + "").equals(selectCheck)) criteria.andEqualTo("status", 1);
       if ((SelectCheckEnum.REJECT + "").equals(selectCheck)) criteria.andEqualTo("status", 2);
    }

    private void sortBuilder(Example example, String sort) {
        example.setOrderByClause("create_time asc");
        if ("DESC".equals(sort))
            example.setOrderByClause("id desc");
    }

    private void searchBuilder(Example.Criteria criteria, String name, String value) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) return;
        criteria.andLike(name, "%" + value + "%");
    }

    private void setCatalogName(PmsProductInfo pmsProductInfoResult) {
        // 获取一级分类名字
        PmsBaseCatalog1 pmsBaseCatalog1Query = PmsBaseCatalog1.builder().id(pmsProductInfoResult.getCatalog1Id()).build();
        PmsBaseCatalog1 pmsBaseCatalog1Result = pmsBaseCatalog1Mapper.selectOne(pmsBaseCatalog1Query);
        pmsProductInfoResult.setCatalog1Name(pmsBaseCatalog1Result.getName());
        // 获取二级分类名字
        PmsBaseCatalog2 pmsBaseCatalog2Query = PmsBaseCatalog2.builder().id(pmsProductInfoResult.getCatalog2Id()).build();
        PmsBaseCatalog2 pmsBaseCatalog2Result = pmsBaseCatalog2Mapper.selectOne(pmsBaseCatalog2Query);
        pmsProductInfoResult.setCatalog2Name(pmsBaseCatalog2Result.getName());
        // 获取三级分类名字
        PmsBaseCatalog3 pmsBaseCatalog3Query = PmsBaseCatalog3.builder().id(pmsProductInfoResult.getCatalog3Id()).build();
        PmsBaseCatalog3 pmsBaseCatalog3Result = pmsBaseCatalog3Mapper.selectOne(pmsBaseCatalog3Query);
        pmsProductInfoResult.setCatalog3Name(pmsBaseCatalog3Result.getName());
    }
}
