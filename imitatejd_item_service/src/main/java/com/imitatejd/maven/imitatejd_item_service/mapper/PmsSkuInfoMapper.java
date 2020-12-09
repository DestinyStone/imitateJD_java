package com.imitatejd.maven.imitatejd_item_service.mapper;

import bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/21 12:19
 * @Description:
 */
@Repository
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectBySpuIds(@Param("pmsProductIds") String[] pmsProductIds);

    Integer selectByValueIds(@Param("spuId") String spuId, @Param("valueIds") String[] valueIds);

    List<PmsSkuInfo> selectByIds(@Param("skuIds") String[] skuIds);

    List<PmsSkuInfo> selectAllByDefaultImage();
}
