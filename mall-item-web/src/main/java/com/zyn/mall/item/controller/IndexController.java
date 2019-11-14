package com.zyn.mall.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhaoyanan
 * @create 2019-11-14-14:03
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(ModelMap modelMap) {

        modelMap.addAttribute("key", "hello world");
        return "index";
    }
}
