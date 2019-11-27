package com.zyn.mall.api.bean.search;

import com.zyn.mall.api.bean.sku.PmsSkuAttrValue;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-27-15:07
 */
@Data
public class PmsSearchParam implements Serializable {

    private String catalog3Id;
    private String keyword;
    private List<PmsSkuAttrValue> skuAttrValueList;
}
