package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.spu.PmsProductImage;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-13-16:22
 */
public interface PmsProductImageService {

    /**
     * 通过spuId查询此spu下的所有图片
     * @param spuId
     * @return
     */
    List<PmsProductImage> spuImageList(String spuId);
}
