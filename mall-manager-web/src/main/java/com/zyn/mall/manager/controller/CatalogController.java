package com.zyn.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.catalog.PmsBaseCatalog1;
import com.zyn.mall.api.bean.catalog.PmsBaseCatalog2;
import com.zyn.mall.api.bean.catalog.PmsBaseCatalog3;
import com.zyn.mall.api.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-08-18:32
 */
@Controller
@CrossOrigin  //在相应端阶段端口不同的跨域问题
public class CatalogController {

    @Reference
    private CatalogService catalogService1;

    @Reference
    private CatalogService catalogService2;

    @Reference
    private CatalogService catalogService3;



    @RequestMapping(value = "/getCatalog1", method = RequestMethod.POST)
    @ResponseBody  //返回json格式
    public List<PmsBaseCatalog1> getCatalog1() {

        List<PmsBaseCatalog1> pmsBaseCatalog1List = catalogService1.getCatalog1();

        return pmsBaseCatalog1List;
    }

    //http://127.0.0.1:8081/getCatalog2?catalog1Id=2
    @RequestMapping(value = "/getCatalog2", method = RequestMethod.POST)
    @ResponseBody  //返回json格式
    public List<PmsBaseCatalog2> getCatalog2(@RequestParam(name = "catalog1Id", required = false) String catalog1Id) {

        List<PmsBaseCatalog2> pmsBaseCatalog2List = catalogService2.getCatalog2(catalog1Id);

        return pmsBaseCatalog2List;
    }

    //http://127.0.0.1:8081/getCatalog2?catalog1Id=2
    @RequestMapping(value = "/getCatalog3", method = RequestMethod.POST)
    @ResponseBody  //返回json格式
    public List<PmsBaseCatalog3> getCatalog3(@RequestParam(name = "catalog2Id", required = false) String catalog2Id) {

        List<PmsBaseCatalog3> pmsBaseCatalog3List = catalogService3.getCatalog3(catalog2Id);

        return pmsBaseCatalog3List;
    }


}
