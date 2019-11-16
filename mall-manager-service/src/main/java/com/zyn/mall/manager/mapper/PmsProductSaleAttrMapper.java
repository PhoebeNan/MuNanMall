package com.zyn.mall.manager.mapper;

import com.zyn.mall.api.bean.spu.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-13-9:47
 */
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

    /**
     * 通过自己书写的sql语句一对多关联查询获取数据
     * @param spuId
     * @param skuId
     * @return
     */
    List<PmsProductSaleAttr> isCheckedMapper(@Param("spuId") String spuId, @Param("skuId") String skuId);
}
