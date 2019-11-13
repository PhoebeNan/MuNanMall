package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttrValue;
import com.zyn.mall.api.service.PmsProductSaleAttrService;
import com.zyn.mall.manager.mapper.PmsProductSaleAttrMapper;
import com.zyn.mall.manager.mapper.PmsProductSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-13-14:05
 */
@Service
public class PmsProductSaleAttrImpl implements PmsProductSaleAttrService {

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);

        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);


        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {

            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = new ArrayList<>();

            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();

            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId()); //pmsProductSaleAttr.getSaleAttrId()得到的是pms_base_sale_attr表中的id

            pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }
}
