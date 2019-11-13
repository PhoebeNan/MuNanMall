package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.base.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.*;
import com.zyn.mall.api.service.SpuProductService;
import com.zyn.mall.manager.mapper.*;
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

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

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

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {

        //保存产品信息
        spuProductInfoMapper.insertSelective(pmsProductInfo);

        //获取商品的主键，此主键是销售属性，销售属性值，图片属性的外键
        String productInfoId = pmsProductInfo.getId();

        //保存销售属性
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            pmsProductSaleAttr.setProductId(productInfoId);
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

            //保存销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(productInfoId);
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
        //保存图片
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(productInfoId);
            pmsProductImageMapper.insertSelective(pmsProductImage);
        }
    }
}
