package com.zyn.mall.mallpayment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zyn.mall.api.bean.payment.PaymentInfo;
import com.zyn.mall.api.service.PaymentService;
import com.zyn.mall.mallpayment.mapper.PaymentInfoMapper;
import com.zyn.mall.mq.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {

        //根据系统外部订单号进行更新
        String orderSn = paymentInfo.getOrderSn();
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", orderSn);

        //调用消息队列
        Connection connection = null;
        Session session = null;

        try {
            ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

            Map<String, Object> map = mqCreateSession(connectionFactory);
            session = (Session) map.get("session");
            connection = (Connection) map.get("connection");

            //支付成功后，引起的的系统各种服务(订单服务的更新-》库存服务-》物流服务)
            paymentInfoMapper.updateByExample(paymentInfo, example);

            mqSendMessage(session, paymentInfo, null,5);

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

    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo,int count) {

        //调用消息队列
        Connection connection = null;
        Session session = null;
        ConnectionFactory connectionFactory = null;

        try {
            connectionFactory = activeMQUtil.getConnectionFactory();

            Map<String, Object> map = mqCreateSession(connectionFactory);
            session = (Session) map.get("session");
            connection = (Connection) map.get("connection");

            //向消息中间件发送一个检查支付状态（支付服务进行消费）的延迟消息队列
            mqSendMessage(session, null, outTradeNo,count);

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

    @Override
    public Map<String, Object> checkAlipayPayment(String out_trade_no) {

        //创建响应参数对象
        Map<String, Object> responseMap = new HashMap<>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //创建请求参数对象
        Map<String, Object> requsetMap = new HashMap<>();
        requsetMap.put("out_trade_no", out_trade_no);

        request.setBizContent(JSON.toJSONString(requsetMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("又可能交易已创建，调用成功");
            responseMap.put("trade_no", response.getTradeNo());
            responseMap.put("out_trade_no",response.getOutTradeNo());
            responseMap.put("trade_status",response.getTradeStatus());
        } else {
            System.out.println("又可能交易未创建，调用失败");
        }

        return responseMap;
    }

    private static Map<String, Object> mqCreateSession(ConnectionFactory connectionFactory) {

        if (connectionFactory != null) {
            //调用消息队列
            Session session = null;
            Connection connection = null;
            Map<String, Object> map = new HashMap<>();
            try {

                connection = connectionFactory.createConnection();

                session = connection.createSession(true, Session.SESSION_TRANSACTED);

            } catch (JMSException e) {
                e.printStackTrace();
            }

            map.put("session", session);
            map.put("connection", connection);

            return map;
        }
        return null;
    }

    private static void mqSendMessage(Session session, PaymentInfo paymentInfo, String outTradeNo,int count) {

        if (session != null) {
            Queue payment_success_queue = null;
            MessageProducer producer = null;
            MapMessage mapMessage = new ActiveMQMapMessage();//hash结构消息
            try {

                if (paymentInfo != null) {
                    payment_success_queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
                    producer = session.createProducer(payment_success_queue);

//            ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();//字符串文本消息

                    mapMessage.setString("out_trade_no", paymentInfo.getOrderSn());
                }
                if (StringUtils.isNotBlank(outTradeNo)) {
                    payment_success_queue = session.createQueue("PAYMENT_CHECK_QUEUE");
                    producer = session.createProducer(payment_success_queue);

//            ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();//字符串文本消息
                    mapMessage.setString("out_trade_no", outTradeNo);
                    mapMessage.setInt("count", count);
                    //延迟队列，延迟生效队列，延迟时间
                    mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * 40);
                }

//            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                producer.send(mapMessage);

                session.commit();

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
