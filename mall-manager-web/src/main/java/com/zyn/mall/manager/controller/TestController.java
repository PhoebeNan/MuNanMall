package com.zyn.mall.manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author NanCoder
 * @create 2020-03-18-16:22
 */
@RestController
public class TestController {

    @RequestMapping("/set")
    public String t1(HttpSession session){
        session.setAttribute("key", "123");

        return "success";
    }

    @RequestMapping("/get")
    public String t2(HttpSession session){
        String key = (String) session.getAttribute("key");

        System.out.println(key);

        return "success";
    }
}
