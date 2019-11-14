package com.zyn.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.service.PmsSkuInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhaoyanan
 * @create 2019-11-13-13:57
 */
@Controller
@CrossOrigin
public class SkuController {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    //saveSkuInfo
    @RequestMapping("/saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){

        pmsSkuInfoService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }
}
