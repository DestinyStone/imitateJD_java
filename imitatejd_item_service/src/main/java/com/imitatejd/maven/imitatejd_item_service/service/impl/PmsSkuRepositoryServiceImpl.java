package com.imitatejd.maven.imitatejd_item_service.service.impl;

import bean.PmsSkuRepository;
import com.imitatejd.maven.imitatejd_item_service.mapper.PmsSkuRepositoryMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsSkuRepositoryService;
import tk.mybatis.mapper.entity.Example;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/21 17:02
 * @Description:
 */
@Service
public class PmsSkuRepositoryServiceImpl implements PmsSkuRepositoryService {

    @Autowired
    private PmsSkuRepositoryMapper pmsSkuRepositoryMapper;

    @Override
    public Message updateRepositoryTotalBySkuId(String skuId, String repositoryTotal) {
        PmsSkuRepository pmsSkuRepositoryQuery = PmsSkuRepository.builder().repositoryTotal(new Integer(repositoryTotal)).build();
        try {
            Example example = new Example(PmsSkuRepository.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("skuId", skuId);

            pmsSkuRepositoryMapper.updateByExampleSelective(pmsSkuRepositoryQuery, example);
            return new Message(ResponseCodeType.SUCCESS, null, true);
        }catch (Exception e) {
            e.printStackTrace();
            return new Message(ResponseCodeType.UN_KNOW_ERROR, null, false);
        }
    }
}
