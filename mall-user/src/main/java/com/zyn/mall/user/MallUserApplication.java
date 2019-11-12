package com.zyn.mall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.zyn.mall.user.mapper")  //添加了mapper包的扫描
public class MallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallUserApplication.class, args);
    }

}
