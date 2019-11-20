package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.base.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductInfo;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-11-14:06
 */

public interface SpuProductService{

    /**
     * 通过三级分类id查询spu的商品信息列表
     * @param catalog3Id
     * @return
     */
    List<PmsProductInfo> spuList(String catalog3Id);

    /**
     * 查询所有销售属性的key值
     * @return
     */
    List<PmsBaseSaleAttr> baseSaleAttrList();

    /**
     * 把商品的元数据信息保存到数据库中
     * @param pmsProductInfo
     */
    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    /**
     * 通过spuId查询此spu下所有销售属性以及销售属性值组合的列表
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String spuId,String skuId);

}
