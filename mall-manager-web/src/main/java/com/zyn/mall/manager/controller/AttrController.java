package com.zyn.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.base.PmsBaseAttrInfo;
import com.zyn.mall.api.bean.base.PmsBaseAttrValue;
import com.zyn.mall.api.service.AttrInfoService;
import com.zyn.mall.api.service.AttrValueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-09-11:39
 */
@Controller
@CrossOrigin  //在相应端阶段端口不同的跨域问题
public class AttrController {

    @Reference
    private AttrInfoService attrInfoService;

    @Reference
    private AttrValueService attrValueService;

    //1.通过PmsBaseAttrInfo三级分类的id查询其下的所有平台属性
    //http://127.0.0.1:8081/attrInfoList?catalog3Id=207
    @RequestMapping(value = "/attrInfoList", method = RequestMethod.GET)
    @ResponseBody  //返回json格式
    public List<PmsBaseAttrInfo> getAttrInfoList(@RequestParam(name = "catalog3Id", required = false) String catalog3Id) {

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoService.getAttrInfoList(catalog3Id);

        return pmsBaseAttrInfos;
    }

    //http://127.0.0.1:8081/saveAttrInfo
    @RequestMapping(value = "/saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {

        String success = attrInfoService.saveAttrInfo(pmsBaseAttrInfo);

        return "redirect:/attrInfoList";
    }

    //http://127.0.0.1:8081/getAttrValueList?attrId=43
    @RequestMapping(value = "/getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {

        List<PmsBaseAttrValue> pmsBaseAttrValues = attrValueService.getAttrValueList(attrId);

        return pmsBaseAttrValues;
    }
}
