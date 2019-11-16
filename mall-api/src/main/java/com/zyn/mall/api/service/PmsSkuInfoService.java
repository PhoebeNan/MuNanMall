package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.sku.PmsSkuInfo;

/**
 * @author zhaoyanan
 * @create 2019-11-14-9:46
 */
public interface PmsSkuInfoService {

    /**
     * 保存某一个spu下的sku数据信息
     * @param pmsSkuInfo
     */
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 通过skuId查找对应的skuInfo数据
     * @param skuId
     * @return
     */
    PmsSkuInfo skuInfoBySkuId(String skuId);
}
