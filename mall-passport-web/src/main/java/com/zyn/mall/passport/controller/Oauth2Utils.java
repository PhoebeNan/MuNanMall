package com.zyn.mall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.zyn.mall.util.HttpclientUtil;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-12-09-16:04
 */
public class Oauth2Utils {


    public static String getCode() {

        //https://api.weibo.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI
//        App Key：
//        1525888287
//
//        App Secret：
//        90d29b930aea4e665ca54384515c25ad
        //1.授权
        //http://passport.mall.com:8085/vlogin


        String grantUrl = "https://api.weibo.com/oauth2/authorize?client_id=1525888287&response_type=code&redirect_uri=http://passport.mall.com:8085/vlogin";
        String code = null;

        try {
            List<String> list = HttpclientUtil.doGet(grantUrl);

            code = list.get(0);


        } catch (Exception e) {
            e.printStackTrace();
        }

        //2.返回授权码到回调地址
        // http://passport.mall.com:8085/vlogin?code=67fcc67a7a7444051a8a429148695877
        return code;
    }

    //3.用授权码code换取access_token
    public static String getAccess_token() {


        //https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE


        String s2Url = "https://api.weibo.com/oauth2/access_token?";


        String appKey = "1525888287";
        String appSecret = "90d29b930aea4e665ca54384515c25ad";
        String redirect_uri = "http://passport.mall.com:8085/vlogin";
        String code = "c6275e1f45f53c05b142d1f694bcd089";

        Map<String,String> map = new HashMap<>();
        map.put("client_id",appKey);
        map.put("client_secret",appSecret);
        map.put("grant_type","authorization_code");
        map.put("redirect_uri",redirect_uri);
        map.put("code",code);

        Map acess_token_map = new HashMap();
        try {
            List<String> list = HttpclientUtil.doPost(s2Url, map);

            String access_token_json = list.get(0);

            acess_token_map = JSON.parseObject(access_token_json, Map.class);

            System.out.println(acess_token_map.get("uid"));
            System.out.println(acess_token_map.get("access_token"));  //2.00AXHd3G6PTQfBa6ff34ad0d0onwIu

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return (String) acess_token_map.get("access_token");
    }

    //4.用access_token换取用户信息
    public static Map<String,String> getUser_info() {

        //https://api.weibo.com/2/users/show.json?acess_token=2.00AXHd3G6PTQfBa6ff34ad0d0onwIu&uid=1

        String s3Url = "https://api.weibo.com/2/users/show.json?access_token=2.00AXHd3G6PTQfBa6ff34ad0d0onwIu&uid=5831200130";

        List<String> list = null;
        try {
            list = HttpclientUtil.doGet(s3Url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String user_json = list.get(0);

        Map<String,String> user_map = JSON.parseObject(user_json, Map.class);

        String toJSONString = JSON.toJSONString(user_map);
        System.out.println(toJSONString);

        return user_map;
    }

    public static void main(String[] args) throws Exception {

        getUser_info();

    }
}

