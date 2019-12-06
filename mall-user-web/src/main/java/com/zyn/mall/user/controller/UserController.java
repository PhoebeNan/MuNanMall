package com.zyn.mall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.user.UmsMember;
import com.zyn.mall.api.bean.user.UmsMemberReceiveAddress;
import com.zyn.mall.api.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * @author zhaoyanan
 * @create 2019-10-30-8:42
 */
@Controller
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/receviceAddress")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){

        List<UmsMemberReceiveAddress> umsMembers= userService.getReceiveAddressByMemberId(memberId);
        return umsMembers;
    }

    @RequestMapping("/getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }

    @RequestMapping("/index")
    @ResponseBody
    public String index(){
        return "hello world";
    }

}
