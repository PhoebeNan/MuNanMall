package com.zyn.mall.passport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ①给用户颁发通行证（token）
 * ②验证其他业务功接收token的真伪
 * @author zhaoyanan
 * @create 2019-12-05-13:36
 */
@Controller
public class PassportController {

    @RequestMapping("index")
    public String index(){

        return "index";
    }
}
