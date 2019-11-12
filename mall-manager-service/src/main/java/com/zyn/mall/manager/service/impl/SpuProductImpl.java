package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.spu.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductInfo;
import com.zyn.mall.api.service.SpuProductService;
import com.zyn.mall.manager.mapper.PmsBaseSaleAttrMapper;
import com.zyn.mall.manager.mapper.SpuProductInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-11-14:08
 */
@Service
public class SpuProductImpl implements SpuProductService {

    @Autowired
    private SpuProductInfoMapper spuProductInfoMapper;

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {

        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = spuProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {

        return pmsBaseSaleAttrMapper.selectAll();
    }
}
