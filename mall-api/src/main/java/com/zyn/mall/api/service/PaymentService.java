package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.payment.PaymentInfo;

import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-12-19-8:20
 */
public interface PaymentService {

    /**
     * 保存支付信息，状态等
     * @param paymentInfo
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新用户的支付状态
     * @param paymentInfo
     */
    void updatePayment(PaymentInfo paymentInfo);

    /**
     * 向消息中间件发送一个检查支付状态（支付服务进行消费）的延迟消息队列
     * @param outTradeNo
     */
    void sendDelayPaymentResultCheckQueue(String outTradeNo,int count);

    /**
     * 进行延迟检查，调用支付检查的接口服务
     * @param out_trade_no
     * @return
     */
    Map<String, Object> checkAlipayPayment(String out_trade_no);
}
