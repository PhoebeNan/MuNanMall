package com.zyn.mall.api.bean.sku;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @param
 * @return
 */
@Data
public class PmsSkuInfo implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    String id;

    @Column
    String productId;

    @Column
    BigDecimal price;

    @Column
    String skuName;

    @Column
    BigDecimal weight;

    @Column
    String skuDesc;

    @Column
    String catalog3Id;

    @Column
    String skuDefaultImg;

    @Transient
    List<PmsSkuImage> pmsSkuImageList;

    @Transient
    List<PmsSkuAttrValue> pmsSkuAttrValueList;

    @Transient
    List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList;

}
