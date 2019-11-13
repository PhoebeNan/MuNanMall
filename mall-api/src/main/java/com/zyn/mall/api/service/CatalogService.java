package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.catalog.PmsBaseCatalog1;
import com.zyn.mall.api.bean.catalog.PmsBaseCatalog2;
import com.zyn.mall.api.bean.catalog.PmsBaseCatalog3;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-08-18:56
 */
public interface CatalogService {

    /**
     *  查询一级分类的所有内容
     * @return
     */
    List<PmsBaseCatalog1> getCatalog1();

    /**
     * 通过传入一级分类ID查询二级分类列表
     * @param catalog1Id
     * @return
     */
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 通过传入二级分类ID查询三级分类列表
     * @param catalog2Id
     * @return
     */
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
