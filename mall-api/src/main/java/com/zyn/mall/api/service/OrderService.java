package com.zyn.mall.api.service;

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
}
