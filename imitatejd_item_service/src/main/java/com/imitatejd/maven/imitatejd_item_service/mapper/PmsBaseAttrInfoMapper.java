package com.imitatejd.maven.imitatejd_item_service.mapper;

import bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/12 10:17
 * @Description:
 */
@Repository
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {

    List<PmsBaseAttrInfo> selectBySkuIds(@Param("ids") String[] ids, @Param("useSpuId") String id);
}
