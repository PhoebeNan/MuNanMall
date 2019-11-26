package com.zyn.mall.api.bean.sku;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-26-13:58
 */
@Data
public class PmsSearchSkuInfo {

    @Id
    private String id;
    private String skuName;
    private String skuDesc;
    private String catalog3Id;
    private BigDecimal price;
    private String skuDefaultImg;
    private double hotScore;
    private String productId;
    private List<PmsSkuAttrValue> skuAttrValueList;
}
