package com.zyn.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.spu.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductInfo;
import com.zyn.mall.api.service.SpuProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-11-13:58
 */
@Controller
@CrossOrigin
public class SpuController {

    @Reference
    private SpuProductService spuProductService;

    //http://127.0.0.1:8081/spuList?catalog3Id=61
    @RequestMapping(value = "/spuList",method = RequestMethod.GET)
    @ResponseBody
    public List<PmsProductInfo> spuList(@RequestParam(name = "catalog3Id") String catalog3Id){

        List<PmsProductInfo> pmsProductInfos = spuProductService.spuList(catalog3Id);
        return pmsProductInfos;
    }
    //baseSaleAttrList spu下的销售属性名称
    @RequestMapping(value = "/baseSaleAttrList",method = RequestMethod.POST)
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){

        List<PmsBaseSaleAttr> pmsBaseSaleAttrList = spuProductService.baseSaleAttrList();
        return pmsBaseSaleAttrList;
    }

    //fileUpload
    //https://m.360buyimg.com/babel/jfs/t5137/20/1794970752/352145/d56e4e94/591417dcN4fe5ef33.jpg
    @RequestMapping("/fileUpload")
    @ResponseBody
    public String fileUpLoad(@RequestParam(name = "file")MultipartFile multipartFile){

        //把图片或视频的数据对象上传到分布式文件存储系统，把元数据存储到数据库中

        //从分布式文件存储系统中获取图片的路径并返回给前端页面
        String imageUrl = "https://m.360buyimg.com/babel/jfs/t5137/20/1794970752/352145/d56e4e94/591417dcN4fe5ef33.jpg";
        return imageUrl;
    }

    //saveSpuInfo
    @RequestMapping("/saveSpuInfo")
    @ResponseBody
    public List<PmsProductInfo> saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){


        return null;
    }

}
