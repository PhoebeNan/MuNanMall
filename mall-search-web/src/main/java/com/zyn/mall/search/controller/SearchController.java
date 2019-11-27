package com.zyn.mall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.search.PmsSearchParam;
import com.zyn.mall.api.bean.search.PmsSearchSkuInfo;
import com.zyn.mall.api.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-27-12:25
 */
@Controller
@CrossOrigin
public class SearchController {

    @Reference
    private SearchService searchService;

    //http://search.gmall.com:8083/list.html?catalog3Id=61
    //参数包括搜索的keyword，平台属性值和三级分类id
    @RequestMapping("/list.html")
    public String lists(PmsSearchParam pmsSearchParam, ModelMap modelMap) {

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);

        modelMap.addAttribute("skuLsInfoList", pmsSearchSkuInfos);
        return "list";
    }

    @RequestMapping("/search")
    public String searchIndex() {

        return "index";
    }

}
