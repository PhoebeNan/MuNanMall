package com.zyn.mall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhaoyanan
 * @create 2019-11-30-10:56
 */
@Controller
@CrossOrigin
public class CartController {

    @RequestMapping("addToCart")
    public String addToCart(){


        return "redirect:/success.html";
    }

}
