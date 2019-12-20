package com.zyn.mall.order.service;

import com.zyn.mall.api.bean.order.OmsOrder;
import com.zyn.mall.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author zhaoyanan
 * @create 2019-12-20-11:18
 */
@Component
public class OrderServiceMqListener {

    @Autowired
    private OrderService orderService;

    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage){

        try {
            String out_trade_no = mapMessage.getString("out_trade_no");
            System.out.println(out_trade_no);

            //更新订单状态业务
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setOrderSn(out_trade_no);

            orderService.updateOrderStatus(omsOrder);
            System.out.println("111");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
