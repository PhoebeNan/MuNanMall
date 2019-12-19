package com.zyn.mall.api.service;

import com.zyn.mall.api.bean.payment.PaymentInfo;

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
}
