package com.zyn.mall.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 配置redis缓存池，获取池中的redis对象
 * @author zhaoyanan
 * @create 2019-11-18-14:39
 */
public class RedisUtils {

    private JedisPool jedisPool;

    public void initPool(String host,int port,int database,String password){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setMaxIdle(30);
        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(true);
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        //在获取连接的时候检查有效性, 默认false
        jedisPoolConfig.setTestOnBorrow(true);
        this.jedisPool = new JedisPool(jedisPoolConfig,host,port,20*1000,password);

    }

    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

//    public void close(Jedis jedis){
//        jedis.close();
//    }
}
