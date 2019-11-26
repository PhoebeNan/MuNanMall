package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.sku.PmsSkuInfo;

import java.util.List;

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
    PmsSkuInfo skuInfoBySkuIdFromRedis(String skuId,String ip);

    /**
     * 通过spuId获取每一个sku下的所有销售属性值列表
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 在Redis缓存数据库中通过skuId查找对应的skuInfo数据
     * @param skuId
     * @return
     */
    PmsSkuInfo skuInfoBySkuIdFromDb(String skuId);

    /**
     * 通过三级分类id查询出所有sku商品数据
     * @param catalog3Id
     * @return
     */
    List<PmsSkuInfo> getSkuAll(String catalog3Id);
}
