package com.imitatejd.maven.imitatejd_order_service.mapper;

import bean.OmsOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 12:53
 * @Description:
 */
public interface OmsOrderMapper extends Mapper<OmsOrder> {
    void updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);

    List<OmsOrder> selectContainOrderItem(@Param("orderIds") String[] orderIds);

    List<OmsOrder> selectByUserIdAndStatusAndSkuName(@Param("userId") String userId, @Param("filter") String filter, @Param("search") String search);
}
