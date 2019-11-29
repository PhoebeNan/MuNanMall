package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.base.PmsBaseAttrInfo;

import java.util.List;
import java.util.Set;

/**
 * @author zhaoyanan
 * @create 2019-11-09-11:03
 */
public interface AttrInfoService {

    /**
     * 通过PmsBaseAttrInfo三级分类的id查询其下的所有平台属性
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

    /**
     * 传入从es中查询出来的所有sku的id的set集合，得到平台属性列表集合
     * @param set
     * @return
     */
    List<PmsBaseAttrInfo> getAttrInfoListByValueIdFormDb(Set<String> set);
}
