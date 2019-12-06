package com.zyn.mall.test;

import com.zyn.mall.utils.JwtUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-12-06-10:04
 */
public class TestJwt {

    public static void main(String[] args) {

        //服务器端自定义的key值
        String key = "MuNanMall";

        //私有部分，用户相关信息
        Map<String,Object> map = new HashMap<>();
        map.put("memberId", "1");
        map.put("nickName", "会员");

        //浏览器部分
        String ip = "127.0.0.1";
        String time = new SimpleDateFormat("yyyy-MM-DD:HH:mm:ss").format(new Date());

        String salt = ip+time;
        String token = JwtUtil.encode(key, map, salt);

        System.out.println(token);
    }
}
