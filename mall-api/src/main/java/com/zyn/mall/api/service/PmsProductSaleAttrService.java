package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-13-14:02
 */
public interface PmsProductSaleAttrService {

    /**
     * 通过spuId查询此spu下的所有销售属性值
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);
}
