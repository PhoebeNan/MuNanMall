package com.zyn.mall.controller;

import com.zyn.mall.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyanan
 * @create 2019-11-20-14:11
 */
@Controller
@CrossOrigin
public class RedissonTest {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("lock")
    @ResponseBody
    public String redissonTest() {

        Jedis jedis = null;
        RLock lock = redissonClient.getLock("lock"); //生命锁

        lock.lock(10, TimeUnit.SECONDS); //上锁
        try {
            jedis = redisUtils.getJedis();
            String v = jedis.get("k");
            if (StringUtils.isBlank(v)) {
                v = "1";
            }
            System.out.println("-->"+v);
            jedis.set("k", (Long.parseLong(v)+1)+"");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
            lock.unlock();//解锁
        }
        return "success";
    }
}
