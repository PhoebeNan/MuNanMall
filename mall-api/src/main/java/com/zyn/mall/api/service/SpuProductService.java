package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.spu.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductInfo;

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

}
