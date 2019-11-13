package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.spu.PmsProductImage;
import com.zyn.mall.api.service.PmsProductImageService;
import com.zyn.mall.manager.mapper.PmsProductImageMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-13-16:24
 */
@Service
public class PmsProductImageImpl implements PmsProductImageService {

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);

        return pmsProductImages;
    }
}
