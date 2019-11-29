package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.base.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductImage;
import com.zyn.mall.api.bean.spu.PmsProductInfo;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttrValue;
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

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String spuId,String skuId) {

        //第一种方式获取isChecked值时性能低下，需要多次查询数据库
//        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//
//        //查询一个spuId下所有销售属性列表
//        pmsProductSaleAttr.setProductId(spuId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
//        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
//            //获取此spu下每一个销售属性的id
//            String saleAttrId = productSaleAttr.getSaleAttrId();
//
//            //查询一个spuId下所有销售属性值列表
//            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//            pmsProductSaleAttrValue.setProductId(spuId);
//            pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//
//            //把销售属性值列表设置到实体类中
//            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//        }

        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.isCheckedMapper(spuId,skuId);

        return pmsProductSaleAttrs;
    }

}
