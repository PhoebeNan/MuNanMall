package com.zyn.mall;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @author zhaoyanan
 * @create 2019-11-19-14:24
 */
public class RedisTest01 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.157.130",6379);
        jedis.auth("123456");
        System.out.println(jedis.ping());
        jedis.close();
    }

    /**
     * redis中的命令，jedis对象中都有对应的方法
     * 测试字符串
     */
    @Test
    public void t1(){
        Jedis jedis = new Jedis("192.168.157.130",6379);
        jedis.auth("123456");
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        System.out.println(jedis.get("b"));
        jedis.close();
    }

    /**
     * 需求：判断key值是否存在，若存在，从redis缓存中查询得到，若不存在，从数据库中查询出来，并存入redis数据库
     */
    @Test
    public void t2(){
        Jedis jedis = new Jedis("192.168.157.130",6379);
        jedis.auth("123456");

        String key = this.getClass().getName();
//        System.out.println(key);
        if(jedis.exists(key)){
            String value = jedis.get(key);
            System.out.println("这是从缓存中查询到的数据："+value);
        }else {
            //这代表第一次查询，缓存中没有
            String result = "从mysql中查询出来的数据";
            jedis.set(key, result);
            System.out.println("第一次请求从mysql数据库中查询出来的结果："+result);
        }
        jedis.close();
    }
}
