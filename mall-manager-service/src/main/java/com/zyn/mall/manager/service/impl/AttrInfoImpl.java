package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.base.PmsBaseAttrInfo;
import com.zyn.mall.api.bean.base.PmsBaseAttrValue;
import com.zyn.mall.api.service.AttrInfoService;
import com.zyn.mall.manager.mapper.PmsBaseAttrInfoMapper;
import com.zyn.mall.manager.mapper.PmsBaseAttrValueMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhaoyanan
 * @create 2019-11-09-11:16
 */
@Service
public class AttrInfoImpl implements AttrInfoService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {

        Example example = new Example(PmsBaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);

        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.selectByExample(example);

        List<PmsBaseAttrValue> pmsBaseAttrValueList = new ArrayList<PmsBaseAttrValue>();

        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfoList) {

            //在查询出平台属性的同时，查询出平台属性值
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValueList);
        }
        return pmsBaseAttrInfoList;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        //获取属性的id值，也就是属性值的外键(attrId的值)
        String attrInfoId = pmsBaseAttrInfo.getId();
        //第一次是保存操作
        if (StringUtils.isBlank(attrInfoId)) {
            insertAttrAndValue(pmsBaseAttrInfo);
        } else {
            //第二次是修改操作
            //先更新属性
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id", attrInfoId);
            pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);


            //===========================================================
            //先删除属性值
            Example example2 = new Example(PmsBaseAttrValue.class);
            example2.createCriteria().andEqualTo("attrId",attrInfoId);
            pmsBaseAttrValueMapper.deleteByExample(example2);


//            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
//            pmsBaseAttrValue.setAttrId(attrInfoId);
//            pmsBaseAttrValueMapper.delete(pmsBaseAttrValue);

            //删除完属性值后在进行插入保存操作
            //设置属性值
            for (PmsBaseAttrValue pmsBasevalueInfo : pmsBaseAttrInfo.getAttrValueList()) {

                pmsBasevalueInfo.setAttrId(attrInfoId);
                pmsBaseAttrValueMapper.insertSelective(pmsBasevalueInfo);
            }


            //===========================================================
            //设置属性值 ，更新属性值
            //这种方式对此系统的删除功能无法生效，因为属性值的删除按钮只是在前台删除，并没有发送controller请求
//            for (PmsBaseAttrValue pmsBasevalueInfo : pmsBaseAttrInfo.getAttrValueList()) {
//
//                Example example3 = new Example(PmsBaseAttrValue.class);
//                String id = pmsBasevalueInfo.getId();
//                example3.createCriteria().andEqualTo("id", id);
//                pmsBaseAttrValueMapper.updateByExampleSelective(pmsBasevalueInfo, example3);
//            }

        }

        return "success";
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoListByValueIdFormDb(Set<String> set) {

        //得到所有平台属性的id值
        String valueIdStr = StringUtils.join(set, ",");
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttrInfoListByValueIdFormDb(valueIdStr);

        return pmsBaseAttrInfos;
    }


    private void insertAttrAndValue(PmsBaseAttrInfo pmsBaseAttrInfo) {
        //设置属性
        pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
        String infoId = pmsBaseAttrInfo.getId();

        //设置属性值
        for (PmsBaseAttrValue pmsBasevalueInfo : pmsBaseAttrInfo.getAttrValueList()) {

            pmsBasevalueInfo.setAttrId(infoId);
            pmsBaseAttrValueMapper.insertSelective(pmsBasevalueInfo);
        }
    }

}
