package com.zyn.mall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.user.UmsMember;
import com.zyn.mall.api.service.UserService;
import com.zyn.mall.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zyn.mall.util.MD5Utils.MD5Encode;

/**
 * ①给用户颁发通行证（token）
 * ②验证其他业务功接收token的真伪
 *
 * @author zhaoyanan
 * @create 2019-12-05-13:36
 */
@Controller
public class PassportController {


    @Reference
    private UserService userService;

    /**
     * 目的只是校验token
     *
     * @param token
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token) {

        //通过jwt校验token的真假

        return "success";
    }

    /**
     * 验证用户的账户和密码，成功则颁发token
     *
     * @param umsMember
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UmsMember umsMember) {

        String token = "";

        //调用用户服务，验证用户名和密码是否正确，对信息查询进行处理
        UmsMember umsMember1 = userService.login(umsMember);


        if (umsMember1 != null) {
            //表示登录成功
//            token= "token";

            //通过jwt制作token
            //jwt服务器部分,通过md5算法加密
            String key = "MuNanMall";
            for (int i = 0; i < 621; i++) {

                key = MD5Encode(key, "utf-8");
            }

            //jwt的私有部分，是用户信息
            Map<String, Object> map = new HashMap<>();

            String userId = umsMember1.getId();
            String userNickname = umsMember1.getNickname();
            map.put("userId", userId);
            map.put("userNickname", userNickname);

            //jwt浏览器客户端部分(盐值)，通过md5算法加密
            //浏览器部分
            String ip = request.getHeader("x-forwarded-for");
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }
            String time = new SimpleDateFormat("yyyy-MM-DD:HH:mm:ss").format(new Date());
            String salt = ip+time;

            for (int i = 0; i < 621; i++) {

                salt = MD5Encode(salt, "utf-8");
            }

            //通过jwt获取token
            token = JwtUtil.encode(key, map, salt);


            //将token存入redis一份
            userService.addUserTokenToCache(token,userId);

        } else {
            //登录失败
            token = "fail";
        }

        return token;
    }


    @RequestMapping("index")
    public String index(@RequestParam("returnUrl") String returnUrl, ModelMap modelMap) {

        if (StringUtils.isNotBlank(returnUrl)) {
            modelMap.put("returnUrl", returnUrl);
        }

        return "index";
    }
}
