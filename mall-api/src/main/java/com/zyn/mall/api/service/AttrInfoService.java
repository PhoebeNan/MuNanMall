package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.PmsBaseAttrInfo;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-09-11:03
 */
public interface AttrInfoService {

    /**
     * 通过PmsBaseAttrInfo三级分类的id查询其下的所有属性
     * @param catalog3Id
     * @return
     */
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    /**
     * 保存三级分类下的属性值
     * @param pmsBaseAttrInfo
     * @return
     */
    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);
}
