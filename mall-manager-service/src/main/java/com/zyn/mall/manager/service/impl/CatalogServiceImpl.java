package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.PmsBaseCatalog1;
import com.zyn.mall.api.bean.PmsBaseCatalog2;
import com.zyn.mall.api.bean.PmsBaseCatalog3;
import com.zyn.mall.api.service.CatalogService;
import com.zyn.mall.manager.mapper.PmsBaseCatalog1Mapper;
import com.zyn.mall.manager.mapper.PmsBaseCatalog2Mapper;
import com.zyn.mall.manager.mapper.PmsBaseCatalog3Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-08-19:11
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {

        List<PmsBaseCatalog1> pmsBaseCatalog1List = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1List;
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {

        Example example = new Example(PmsBaseCatalog2.class);
        example.createCriteria().andEqualTo("catalog1Id",catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2List = pmsBaseCatalog2Mapper.selectByExample(example);
        return pmsBaseCatalog2List;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {

        Example example = new Example(PmsBaseCatalog3.class);
        example.createCriteria().andEqualTo("catalog2Id",catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3List = pmsBaseCatalog3Mapper.selectByExample(example);
        return pmsBaseCatalog3List;
    }
}
