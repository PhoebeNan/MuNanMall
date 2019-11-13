package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.base.PmsBaseAttrValue;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-09-14:10
 */
public interface AttrValueService {

    /**
     * 通过PmsBaseAttrValue中attrId值获取此属性的值
     * @param attrId
     * @return
     */
    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
