package com.zyn.mall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.annotations.LoginRequired;
import com.zyn.mall.api.bean.base.PmsBaseAttrInfo;
import com.zyn.mall.api.bean.base.PmsBaseAttrValue;
import com.zyn.mall.api.bean.crumb.PmsSearchCrumb;
import com.zyn.mall.api.bean.search.PmsSearchParam;
import com.zyn.mall.api.bean.search.PmsSearchSkuInfo;
import com.zyn.mall.api.bean.sku.PmsSkuAttrValue;
import com.zyn.mall.api.service.AttrInfoService;
import com.zyn.mall.api.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author zhaoyanan
 * @create 2019-11-27-12:25
 */
@Controller
@CrossOrigin
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private AttrInfoService attrInfoService;

    //http://search.gmall.com:8083/list.html?catalog3Id=61
    //参数包括搜索的keyword，平台属性值和三级分类id
    @RequestMapping("/list.html")
    public String lists(PmsSearchParam pmsSearchParam, ModelMap modelMap) {

        //Set为无序，不重复的集合
        Set<String> set = new HashSet<>();

        //向set集合中添加平台属性值id，以达到去重的目的
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();

                set.add(valueId);
            }
        }

        modelMap.addAttribute("skuLsInfoList", pmsSearchSkuInfos);

        //通过传入平台属性值id集合，得到平台属性列表并返回list.html页面
        //此平台属性值列表是从es的数据库中取出的
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoService.getAttrInfoListByValueIdFormDb(set);

        modelMap.addAttribute("attrList", pmsBaseAttrInfos);

        //得到地址栏url中平台属性值的id
        String[] valueIds = pmsSearchParam.getValueId();


        //使用迭代器作为游标
        Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();


        //在删除属性组的同时就把面包屑功能做出来
        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();

        if (valueIds != null) {
            while (iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();

                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    String valueId1 = pmsBaseAttrValue.getId();


                    //如果delvalueIds不为空，说明请求中包含属性的参数，每一个属性属性参数，都会生成一个面包屑
                    for (String valueId : valueIds) {
                        if (valueId.equals(valueId1)) {

                            PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                            pmsSearchCrumb.setValueId(valueId1);

                            //平台属性值的名字
                            String valueName = pmsBaseAttrValue.getValueName();
                            pmsSearchCrumb.setValueName(valueName);

                            pmsSearchCrumb.setUrlParam(getUrlParm(pmsSearchParam, valueId1));
                            pmsSearchCrumbs.add(pmsSearchCrumb);

                            iterator.remove();
                        }
                    }

                }
            }

        }


        //<li  th:each="attrValue:${attrInfo.attrValueList}"><a th:href="'/list.html?'+${urlParam}+'&valueId='+${attrValue.id}"  th:text="${attrValue.valueName}">属性值</a></li>
        String urlParam = getUrlParm(pmsSearchParam, null);
        modelMap.addAttribute("urlParam", urlParam);

        String keyword = pmsSearchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            modelMap.addAttribute("keyword", keyword);
        }


//        //面包屑功能  与删除属性值进行合并
//        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
//        String[] delvalueIds = pmsSearchParam.getValueId();
//        if (delvalueIds != null) {
//
//            //如果delvalueIds不为空，说明请求中包含属性的参数，每一个属性属性参数，都会生成一个面包屑
//            for (String delvalueId : delvalueIds) {
//                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
//                pmsSearchCrumb.setValueId(delvalueId);
//
//                pmsSearchCrumb.setValueName(delvalueId);
//                pmsSearchCrumb.setUrlParam(getUrlParm(pmsSearchParam, delvalueId));
//                pmsSearchCrumbs.add(pmsSearchCrumb);
//            }
//        }

        modelMap.addAttribute("attrValueSelectedList", pmsSearchCrumbs);

        return "list";
    }

    private String getUrlParm(PmsSearchParam pmsSearchParam, String... delvalueId) {

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (builder != null) {

                builder.append("&");
            }
            builder.append("catalog3Id=" + catalog3Id);
        }
        if (StringUtils.isNotBlank(keyword)) {
            if (builder != null) {

                builder.append("&");
            }
            builder.append("keyword=" + keyword);
        }

        if (skuAttrValueList != null) {
            if (delvalueId != null) {
                for (String valueId : skuAttrValueList) {
                    for (String s : delvalueId) {
                        if (!valueId.equals(s)) {

                            builder.append("&valueId=" + valueId);
                        }
                    }
                }
            } else {
                for (String valueId : skuAttrValueList) {
                    builder.append("&valueId=" + valueId);

                }
            }
        }

        return builder.toString();
    }

    @RequestMapping("/index")
    @LoginRequired(loginSuccess = false)
    public String searchIndex() {

        return "index";
    }

}
