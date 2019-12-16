package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.order.OmsOrder;

/**
 * @author zhaoyanan
 * @create 2019-12-12-14:15
 */
public interface OrderService {


    /**
     * 通过用户id和页面传递过来的tradeCode检查与redis中的交易码是否一致
     * @param memberId
     * @param tradeCode
     * @return
     */
    String checkTradeCode(String memberId, String tradeCode);

    /**
     * 通过用户id生成tradeCode交易码
     * @param memberId
     * @return
     */
    String genertorTradeCode(String memberId);


    /**
     * //将订单和订单详情写入到数据库
     * //删除购物车的对应商品
     * @param omsOrder
     */
    void saveOrder(OmsOrder omsOrder);
}
