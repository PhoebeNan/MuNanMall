package com.zyn.mall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.service.OrderService;
import com.zyn.mall.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

/**
 * @author zhaoyanan
 * @create 2019-12-12-14:22
 */
@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private RedisUtils redisUtils;

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
}
