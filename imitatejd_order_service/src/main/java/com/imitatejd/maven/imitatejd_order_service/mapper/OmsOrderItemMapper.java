package com.imitatejd.maven.imitatejd_order_service.mapper;

import bean.OmsOrderItem;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/1 14:30
 * @Description:
 */
@Repository
public interface OmsOrderItemMapper extends Mapper<OmsOrderItem>, MySqlMapper<OmsOrderItem> {
}
