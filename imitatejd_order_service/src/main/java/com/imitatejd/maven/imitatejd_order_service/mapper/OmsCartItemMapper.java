package com.imitatejd.maven.imitatejd_order_service.mapper;

import bean.OmsCartItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 13:02
 * @Description:
 */
@Repository
public interface OmsCartItemMapper extends Mapper<OmsCartItem> {
    List<OmsCartItem> selectBySkuIdsAndUserId(@Param("userId") String userId, @Param("skuIds") String[] skuIds);

    void decrByRepository(@Param("skuId") String productSkuId, @Param("repository") Integer quantity);
}
