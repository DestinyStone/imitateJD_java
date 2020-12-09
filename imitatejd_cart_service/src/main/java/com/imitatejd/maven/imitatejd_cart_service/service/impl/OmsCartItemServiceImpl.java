package com.imitatejd.maven.imitatejd_cart_service.service.impl;

import bean.OmsCartItem;
import com.imitatejd.maven.imitatejd_cart_service.mapper.OmsCartItemMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.OmsCartItemService;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/31 09:11
 * @Description:
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;

    @Override
    public Message insertBySkuIdAndQuantity(List<OmsCartItem> cartItemListQuery) {
        cartItemListQuery.forEach(omsCartItem -> {
            OmsCartItem omsCartItemQuery = OmsCartItem.builder().productSkuId(omsCartItem.getProductSkuId()).memberId(omsCartItem.getMemberId()).build();
            OmsCartItem omsCartItemResult = omsCartItemMapper.selectOne(omsCartItemQuery);
            if (omsCartItemResult == null) {
                omsCartItemMapper.insertSelective(omsCartItem);
            } else {
                omsCartItemResult.setQuantity(omsCartItem.getQuantity() + omsCartItemResult.getQuantity());
                omsCartItemMapper.updateByPrimaryKeySelective(omsCartItemResult);
            }
        });
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @Override
    public Message selectByUserId(String userId) {
        OmsCartItem omsCartItemQuery = OmsCartItem.builder().memberId(userId).build();
        List<OmsCartItem> omsCartItemList = omsCartItemMapper.select(omsCartItemQuery);
        return new Message(ResponseCodeType.SUCCESS, omsCartItemList, true);
    }

    @Override
    public Message updateCartTotal(String userId, String skuId, Integer skuTotal) {
        OmsCartItem omsCartItem = OmsCartItem.builder().quantity(skuTotal).build();
        Example example = new Example(OmsCartItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("memberId", userId).andEqualTo("productSkuId", skuId);
        omsCartItemMapper.updateByExampleSelective(omsCartItem, example);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }
}
