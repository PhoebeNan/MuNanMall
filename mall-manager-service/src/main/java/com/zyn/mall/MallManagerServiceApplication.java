package com.zyn.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.zyn.mall.manager.mapper")  //添加了mapper包的扫描
public class MallManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallManagerServiceApplication.class, args);
    }

}
