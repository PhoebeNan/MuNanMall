package com.zyn.mall.mallpayment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.payment.PaymentInfo;
import com.zyn.mall.api.service.PaymentService;
import com.zyn.mall.mallpayment.mapper.PaymentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

/**
 * @author zhaoyanan
 * @create 2019-12-19-8:46
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {

        //根据系统外部订单号进行更新
        String orderSn = paymentInfo.getOrderSn();
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);

        paymentInfoMapper.updateByExample(paymentInfo, example);

    }
}
