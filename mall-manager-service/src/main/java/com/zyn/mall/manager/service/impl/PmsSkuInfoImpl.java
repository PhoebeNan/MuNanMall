package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.sku.PmsSkuAttrValue;
import com.zyn.mall.api.bean.sku.PmsSkuImage;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.bean.sku.PmsSkuSaleAttrValue;
import com.zyn.mall.api.service.PmsSkuInfoService;
import com.zyn.mall.manager.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-14-9:48
 */
@Service
public class PmsSkuInfoImpl implements PmsSkuInfoService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        //如何前台传递过来的pmsSkuInfo对象中没有选择图片的默认值，那么后台可以给用户设置一个默认值
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if(StringUtils.isBlank(skuDefaultImg)){

            String imgUrl = pmsSkuInfo.getSkuImageList().get(0).getImgUrl();
            pmsSkuInfo.setSkuDefaultImg(imgUrl);
            pmsSkuInfo.getSkuImageList().get(0).setIsDefault("1");

        }

        //保存skuInfo
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //获取sku主键Id值，此值为下面三个的外键，并赋予设置
        String skuInfoId = pmsSkuInfo.getId();
        //保存此sku下关联的平台属性
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {

            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //保存此sku下关联的销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {

            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        //保存此sku下管理的图片
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();

        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }


    }

    @Override
    public PmsSkuInfo skuInfoBySkuId(String skuId) {

        //商品数据
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo info = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        info.setSpuId(info.getProductId());
        //图片列表
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        info.setSkuImageList(pmsSkuImages);


        return info;
    }
}
