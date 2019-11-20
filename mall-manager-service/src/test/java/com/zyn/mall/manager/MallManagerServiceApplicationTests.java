package com.zyn.mall.manager;

import com.zyn.mall.utils.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallManagerServiceApplicationTests {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void contextLoads() {

        Jedis jedis = redisUtils.getJedis();

        System.out.println(jedis);
        jedis.close();
    }

    @Test
    public void t1(){
        String key = "schools";

        Jedis jedis = redisUtils.getJedis();
        if(jedis.exists(key)){
            Map<String, String> map = jedis.hgetAll(key);
            System.out.println("从redis中查询出来的结果");
            System.out.println(map.get("id")+"\t"+map.get("name")+"\t"+map.get("age"));
        }else {
            //缓存中不存在，从mysql中查询并存储到redis数据库中
            int id = 2;
            String name = "zyn";
            int age = 25;
            jedis.hset(key, "id", "2");
            jedis.hset(key, "name", name);
            jedis.hset(key, "age", "25");

            System.out.println("从MySQL中查询出来并存储到redis");
        }

        jedis.close();
    }

}
