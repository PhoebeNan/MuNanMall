package com.zyn.mall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.order.OmsOrder;
import com.zyn.mall.api.bean.order.OmsOrderItem;
import com.zyn.mall.api.service.CartService;
import com.zyn.mall.api.service.OrderService;
import com.zyn.mall.mq.ActiveMQUtil;
import com.zyn.mall.order.service.mapper.OmsOrderItemMapper;
import com.zyn.mall.order.service.mapper.OmsOrderMapper;
import com.zyn.mall.utils.RedisUtils;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author zhaoyanan
 * @create 2019-12-12-14:22
 */
@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Reference
    private CartService cartService;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {

        ;
        Jedis jedis = null;
        try {
            jedis = redisUtils.getJedis();
            String key = "user:" + memberId + ":tradeCode";

//            String tradeCodeFromCache = jedis.get(key);

            //jedis.eval("lua"); 可以用lua脚本在查询到key的同时删除key，防止高并发下的意外发生
            String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(key),Collections.singletonList(tradeCode));

            if(eval!=null&&eval!=0){

                return "success";
            }else {
                return "fail";
            }

        } finally {
            jedis.close();
        }


    }

    @Override
    public String genertorTradeCode(String memberId) {

        String tradeCode;
        Jedis jedis = null;
        try {
            jedis = redisUtils.getJedis();
            String key = "user:" + memberId + ":tradeCode";
            tradeCode = UUID.randomUUID().toString();

            jedis.setex(key, 60 * 15, tradeCode);

        } finally {
            jedis.close();
        }

        return tradeCode;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {

        //保存订单
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();

        //保存订单详情
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);

            //删除购物车商品数据
            //String productSkuId = omsOrderItem.getProductSkuId();
            //cartService.delCart(productSkuId);
        }
    }

    @Override
    public OmsOrder getOrderOutTradeNo(String outTradeNo) {

        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);

        return omsOrder1;
    }

    @Override
    public void updateOrderStatus(OmsOrder omsOrder) {

        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", omsOrder.getStatus());

        OmsOrder omsOrder1 = new OmsOrder();
        omsOrder1.setStatus(1);



        //发送一个订单已支付的队列，提供给库存消费
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
            //发送一个订单已支付的队列，提供给库存消费
            omsOrderMapper.updateByExampleSelective(omsOrder1, example);

            Queue order_pay_queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(order_pay_queue);

//            ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();//字符串文本消息
            MapMessage mapMessage = new ActiveMQMapMessage();//hash结构消息

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
