package com.zyn.mall.api.bean.cart;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsCartItem implements Serializable{

    @Id
    private String id;
    private String productId;
    private String productSkuId;
    private String memberId;
    private BigDecimal quantity;  //添加到购物车的商品数量
    private BigDecimal price; //添加到购物车的单价
    private String sp1;
    private String sp2;
    private String sp3;
    private String productPic;  //商品主图
    private String productName; //商品名称
    private String productSubTitle; //商品副标题（卖点）
    private String productSkuCode;  //商品sku条码
    private String memberNickname; //会员昵称
    private Date createDate;
    private Date modifyDate;
    private int deleteStatus;
    private String productCategoryId;
    private String productBrand;  //商品的商标
    private String productSn; //商品sku的sn码
    private String productAttr; //商品销售属性的json字符串

    private String isChecked; //商品是否被选中

    @Transient
    private BigDecimal totalPrice; //商品的总价

}
