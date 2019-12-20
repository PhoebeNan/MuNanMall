package com.zyn.mall.mallpayment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.payment.PaymentInfo;
import com.zyn.mall.api.service.PaymentService;
import com.zyn.mall.mallpayment.mapper.PaymentInfoMapper;
import com.zyn.mall.mq.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;

/**
 * @author zhaoyanan
 * @create 2019-12-19-8:46
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

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

        //调用消息队列
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session = null;
        try {
            connectionFactory = activeMQUtil.getConnectionFactory();
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);

        } catch (JMSException e) {
            e.printStackTrace();
        }


        try {
            //支付成功后，引起的的系统各种服务(订单服务的更新-》库存服务-》物流服务)
            paymentInfoMapper.updateByExample(paymentInfo, example);

            Queue payment_success_queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(payment_success_queue);

//            ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();//字符串文本消息
            MapMessage mapMessage = new ActiveMQMapMessage();//hash结构消息
            mapMessage.setString("out_trade_no",paymentInfo.getOrderSn());

//            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            session.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.close();
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
