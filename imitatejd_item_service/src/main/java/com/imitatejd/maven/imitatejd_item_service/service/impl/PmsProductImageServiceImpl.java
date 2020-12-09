package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsProductImage;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsProductImageMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import service.PmsProductImageService;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/27 15:06
 * @Description:
 */
@Service
public class PmsProductImageServiceImpl implements PmsProductImageService {

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

}
