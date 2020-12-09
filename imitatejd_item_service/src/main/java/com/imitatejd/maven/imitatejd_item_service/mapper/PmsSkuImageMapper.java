package com.imitatejd.maven.imitatejd_item_service.mapper;

import bean.PmsSkuImage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/21 12:22
 * @Description:
 */
@Repository
public interface PmsSkuImageMapper extends Mapper<PmsSkuImage> {
    List<PmsSkuImage> selectImageBySkuId(@Param("id") String id);
}
