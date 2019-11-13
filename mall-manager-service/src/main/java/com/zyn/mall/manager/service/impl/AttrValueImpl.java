package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.base.PmsBaseAttrValue;
import com.zyn.mall.api.service.AttrValueService;
import com.zyn.mall.manager.mapper.PmsBaseAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-09-14:16
 */
@Service
public class AttrValueImpl implements AttrValueService {

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {

        Example example = new Example(PmsBaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId",attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByExample(example);

        return pmsBaseAttrValues;
    }
}
