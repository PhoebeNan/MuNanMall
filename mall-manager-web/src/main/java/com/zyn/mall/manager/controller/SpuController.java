package com.zyn.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.base.PmsBaseSaleAttr;
import com.zyn.mall.api.bean.spu.PmsProductImage;
import com.zyn.mall.api.bean.spu.PmsProductInfo;
import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import com.zyn.mall.api.service.PmsProductImageService;
import com.zyn.mall.api.service.PmsProductSaleAttrService;
import com.zyn.mall.api.service.SpuProductService;
import com.zyn.mall.util.FastdfsUtils;
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

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @Reference
    private PmsProductImageService pmsProductImageService;

    //http://127.0.0.1:8081/spuList?catalog3Id=61
    //1.通过三级分类id查询spu的商品信息列表
    @RequestMapping(value = "/spuList",method = RequestMethod.GET)
    @ResponseBody
    public List<PmsProductInfo> spuList(@RequestParam(name = "catalog3Id") String catalog3Id){

        List<PmsProductInfo> pmsProductInfos = spuProductService.spuList(catalog3Id);
        return pmsProductInfos;
    }


    //2.查询所有销售属性的key值
    //baseSaleAttrList spu下的销售属性名称
    @RequestMapping(value = "/baseSaleAttrList",method = RequestMethod.POST)
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){

        List<PmsBaseSaleAttr> pmsBaseSaleAttrList = spuProductService.baseSaleAttrList();
        return pmsBaseSaleAttrList;
    }



    //fileUpload
    //3.上传图片到fastdfs服务器的storage
    //https://m.360buyimg.com/babel/jfs/t5137/20/1794970752/352145/d56e4e94/591417dcN4fe5ef33.jpg
    @RequestMapping("/fileUpload")
    @ResponseBody
    public String fileUpLoad(@RequestParam(name = "file")MultipartFile multipartFile){

        //把图片或视频的数据对象上传到分布式文件存储系统，把元数据存储到数据库中
        //从分布式文件存储系统中获取图片的路径并返回给前端页面
        String imageUpLoadPath = FastdfsUtils.imageUpLoad(this.getClass(), multipartFile);
        //返回前端页面图片存储在fastdfs分布式文件存储系统的路径
        return imageUpLoadPath;
    }



    //saveSpuInfo
    //4.把商品的元数据信息保存到数据库中
    @RequestMapping("/saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        spuProductService.saveSpuInfo(pmsProductInfo);

        return "redirect:/spuList";
    }


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
