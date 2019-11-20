package com.zyn.mall.manager.mapper;

import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-14-9:49
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {

    /**
     * 通过自己书写的sql语句一对多关联查询获取数据
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
