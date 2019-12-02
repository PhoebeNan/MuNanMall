package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.cart.OmsCartItem;

/**
 * @author zhaoyanan
 * @create 2019-12-02-14:16
 */
public interface CartService {

    /**
     * 用户已经登录，通过商品skuId查询出购物车商品数据
     * @param memberId
     * @param skuId
     * @return
     */
    OmsCartItem ifCartExitByUser(String memberId, String skuId);

    /**
     * 将商品数据添加到购物车中
     * @param omsCartItem
     */
    void addCart(OmsCartItem omsCartItem);

    /**
     *  修改购物车中的商品数据
     * @param omsCartItemFromDb
     */
    void setCart(OmsCartItem omsCartItemFromDb);

    /**
     *  通过用户id同步到redis缓存中
     * @param memberId
     */
    void flushCartCache(String memberId);

}
