package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.search.PmsSearchParam;
import com.zyn.mall.api.bean.search.PmsSearchSkuInfo;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-27-15:13
 */
public interface SearchService {

    /**
     * 在商品首页中传递参数，参数包括搜索的keyword，平台属性值和三级分类id
     * @param pmsSearchParam
     * @return 返回搜索到商品的数据到前台
     */
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
