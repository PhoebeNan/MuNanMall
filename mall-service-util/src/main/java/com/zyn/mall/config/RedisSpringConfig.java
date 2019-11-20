package com.zyn.mall.config;

import com.zyn.mall.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置redis与spring容器相关联的配置类
 * @author zhaoyanan
 * @create 2019-11-18-14:40
 */
@Configuration
public class RedisSpringConfig {

    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port;
    @Value("${spring.redis.database:0}")
    private int database;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedisUtils getRedisUtils(){
        if(host.equals("disabled")){
            return null;
        }

        RedisUtils redisUtils = new RedisUtils();
        redisUtils.initPool(host, port, database,password);
        return redisUtils;
    }
}
