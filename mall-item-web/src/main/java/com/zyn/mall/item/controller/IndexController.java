package com.zyn.mall.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-14-14:03
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(ModelMap modelMap) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i,"循环数据"+i);
        }
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("key", "hello world");
        return "index";
    }
}
