package com.zyn.mall.passport.controller;

import com.zyn.mall.api.bean.user.UmsMember;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ①给用户颁发通行证（token）
 * ②验证其他业务功接收token的真伪
 * @author zhaoyanan
 * @create 2019-12-05-13:36
 */
@Controller
public class PassportController {


    /**
     * 目的只是校验token
     * @param token
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token){

        //通过jwt校验token的真假

        return "success";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember){

        return "token";
    }


    @RequestMapping("index")
    public String index(@RequestParam("returnUrl") String returnUrl, ModelMap modelMap){

        if(StringUtils.isNotBlank(returnUrl)){
            modelMap.put("returnUrl",returnUrl);
        }

        return "index";
    }
}
