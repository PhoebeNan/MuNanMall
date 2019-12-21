package com.zyn.mall.mallpayment.mqlistener;

import com.zyn.mall.api.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-12-21-13:12
 */
@Component
public class PaymentServiceMqListener {

    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void consumePaymentCheckRestsult(MapMessage mapMessage) {

        Integer count = 0;
        try {
            String out_trade_no = mapMessage.getString("out_trade_no");
            count = mapMessage.getInt("count");

            //调用paymentService支付宝的检查接口
            //进行延迟检查，调用支付检查的接口服务
            System.out.println("进行延迟检查，调用支付检查的接口服务");
            Map<String, Object> mapResult = paymentService.checkAlipayPayment(out_trade_no);

            if (mapResult != null && !mapResult.isEmpty()) {
                String trade_status = (String) mapResult.get("trade_status");
                //根据查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据和后续任务

                if (StringUtils.isNotBlank(trade_status) && trade_status.equals("TRADE_SUCCESS")) {
                    //已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列
                    System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列");
                    return;
                }
            }

            if (count > 0) {
                //没用支付成功，继续发送延迟检查任务
                System.out.println("没用支付成功，检查剩余次数为：" + count + "继续发送延迟检查任务");
                count--;
                paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
            } else {
                System.out.println("检查剩余次数用尽，结束检查");
            }

//            if (mapResult == null || mapResult.isEmpty()) {
//
//                //没用支付成功，继续发送延迟检查任务
//                paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
//            } else {
//                String trade_status = (String) mapResult.get("trade_status");
//                //根据查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据和后续任务
//
//                if (trade_status.equals("TRADE_SUCCESS")) {
//                    //已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列
//
//                } else {
//                    if (count > 0) {
//                        //没用支付成功，继续发送延迟检查任务
//                        System.out.println("没用支付成功，检查剩余次数为："+count+"继续发送延迟检查任务");
//                        count--;
//                        paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
//                    }else {
//                        System.out.println("检查剩余次数用尽，结束检查");
//                    }
//                }
//            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
