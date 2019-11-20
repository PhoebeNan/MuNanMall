package com.zyn.mall.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("lock")
    @ResponseBody
    public String redissonTest(){

        RLock lock = redissonClient.getLock("lock");
        lock.lock(10, TimeUnit.SECONDS);

        return "success";
    }
}
