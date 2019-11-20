package com.zyn.mall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.bean.sku.PmsSkuSaleAttrValue;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import com.zyn.mall.api.service.PmsSkuInfoService;
import com.zyn.mall.api.service.SpuProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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
    public String item(@PathVariable String skuId, ModelMap modelMap, HttpServletRequest request) {
        //获取请求的ip地址
        String ip;
        String forwarded = request.getHeader("x-forwarded-for");
        if(StringUtils.isNotBlank(forwarded)){
            ip = forwarded;
        }else {
            ip = request.getRemoteAddr();
        }
        //通过skuId查询一个sku商品
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.skuInfoBySkuIdFromRedis(skuId,ip);
        //查询spu下所有sku的销售属性列表的集合
        String spuId = pmsSkuInfo.getSpuId();
        List<PmsProductSaleAttr> spuSaleAttrListCheckBySku = spuProductService.spuSaleAttrListCheckBySku(spuId,skuId);
        modelMap.put("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);
        modelMap.put("skuInfo", pmsSkuInfo);

        //通过查询出来spu下的销售属性值列表的id值和此spu下存在的所有skuId值，将其封装到一个
        //HashMap中，在转换成json字符串传递到前台客户端的js代码中
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoService.getSkuSaleAttrValueListBySpu(spuId);

        HashMap<String, String> skuSaleAttrValueHashMap = new HashMap<>();

        for (PmsSkuInfo skuInfo : pmsSkuInfoList) {
            //每一个sku的销售属性值id在每次 sku循环时都置为空字符串
            String k = "";
            String v = skuInfo.getId();
            //获取每一个sku下所有的销售属性值列表
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                //得到每一个销售属性值列表Id值
                String saleAttrValueId = pmsSkuSaleAttrValue.getSaleAttrValueId();
                k += saleAttrValueId+"|";

            }
            skuSaleAttrValueHashMap.put(k, v);
        }

        String skuSaleAttrValueHashMapJson = JSON.toJSONString(skuSaleAttrValueHashMap);
        modelMap.put("skuSaleAttrValueHashMapJson",skuSaleAttrValueHashMapJson);

        return "item";
    }

}
