package com.zyn.mall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import com.zyn.mall.api.service.PmsSkuInfoService;
import com.zyn.mall.api.service.SpuProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-14-14:03
 */
@Controller
@CrossOrigin
public class ItemController {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private SpuProductService spuProductService;

    /**
     * 测试数据
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/index")
    public String index(ModelMap modelMap) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i, "循环数据" + i);
        }
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("key", "hello world");
        modelMap.addAttribute("check", 1);
        return "index";
    }

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap) {

        //通过skuId查询一个sku商品
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.skuInfoBySkuId(skuId);
        //查询spu下所有sku的销售属性列表的集合
        String spuId = pmsSkuInfo.getSpuId();
        List<PmsProductSaleAttr> spuSaleAttrListCheckBySku = spuProductService.spuSaleAttrListCheckBySku(spuId,skuId);
        modelMap.put("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);
        modelMap.put("skuInfo", pmsSkuInfo);

        return "item";
    }
}
