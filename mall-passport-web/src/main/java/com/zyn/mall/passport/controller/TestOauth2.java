package com.zyn.mall.passport.controller;

import com.zyn.mall.util.HttpclientUtil;

/**
 * @author zhaoyanan
 * @create 2019-12-09-16:04
 */
public class TestOauth2 {

    public static void main(String[] args) {

        //https://api.weibo.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI
//        App Key：
//        1525888287
//
//        App Secret：
//        90d29b930aea4e665ca54384515c25ad
        //1.授权并回调方法
        //http://passport.mall.com:8085/vlogin


        String grantUrl = "https://api.weibo.com/oauth2/authorize?client_id=1525888287&response_type=code&redirect_uri=http://passport.mall.com:8085/vlogin";

        String code = null;
        try {
            code = HttpclientUtil.doGet(grantUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(code);

        // http://passport.mall.com:8085/vlogin?code=f86a60604c4a32d9c60b9754726cf63b

    }
}

