package com.zyn.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.spu.PmsProductImage;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import com.zyn.mall.api.service.PmsProductImageService;
import com.zyn.mall.api.service.PmsProductSaleAttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-13-13:57
 */
@Controller
@CrossOrigin
public class SkuController {

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @Reference
    private PmsProductImageService pmsProductImageService;

    //spuSaleAttrList?spuId=24
    @RequestMapping("/spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){

        List<PmsProductSaleAttr> pmsSkuSaleAttrValues = pmsProductSaleAttrService.spuSaleAttrList(spuId);

        return pmsSkuSaleAttrValues;
    }

    //spuImageList?spuId=68
    @RequestMapping("/spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){

        List<PmsProductImage> productImages = pmsProductImageService.spuImageList(spuId);

        return productImages;
    }
}
