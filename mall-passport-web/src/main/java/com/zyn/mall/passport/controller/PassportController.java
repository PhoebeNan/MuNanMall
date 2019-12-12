package com.zyn.mall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zyn.mall.api.bean.user.UmsMember;
import com.zyn.mall.api.service.UserService;
import com.zyn.mall.util.HttpclientUtil;
import com.zyn.mall.util.MD5Utils;
import com.zyn.mall.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
     * 社交登录功能
     * 第三方网站调用回调方法 http://passport.mall.com:8085/vlogin
     * 返回一个code授权码 http://passport.mall.com:8085/vlogin?code=67fcc67a7a7444051a8a429148695877
     *
     * @param request
     * @param code
     * @return
     */
    @RequestMapping("vlogin")
    public String vlogin(HttpServletRequest request, String code) {

        //2.使用授权码换取access_token

        String s2Url = "https://api.weibo.com/oauth2/access_token";

        String appKey = "1525888287";
        String appSecret = "90d29b930aea4e665ca54384515c25ad";
        String redirect_uri = "http://passport.mall.com:8085/vlogin";

        Map<String, String> map = new HashMap<>();
        map.put("client_id", appKey);
        map.put("client_secret", appSecret);
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", redirect_uri);
        map.put("code", code);

        Map<String, Object> acess_token_map = new HashMap();

        String access_token = null;
        String uid = null;
        try {
            List<String> list = HttpclientUtil.doPost(s2Url, map);

            String access_token_json = list.get(0);

            acess_token_map = JSON.parseObject(access_token_json, Map.class);

//            System.out.println(acess_token_map.get("uid"));

            uid = (String) acess_token_map.get("uid");
            access_token = (String) acess_token_map.get("access_token");

            //System.out.println(access_token);  //2.00AXHd3G6PTQfBa6ff34ad0d0onwIu


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        //3.使用access_token换取用户信息

        //https://api.weibo.com/2/users/show.json?acess_token=2.00AXHd3G6PTQfBa6ff34ad0d0onwIu&uid=1

        String s3Url = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;

        List<String> list = null;
        try {
            list = HttpclientUtil.doGet(s3Url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String user_json = list.get(0);

        Map<String, Object> user_map = JSON.parseObject(user_json, Map.class);


        //4.将用户信息保存到数据库，用户类型设置为微博用户或其他

        UmsMember umsMember = new UmsMember();
        umsMember.setNickname((String) user_map.get("screen_name"));

        int gen = 1;
        String gender = (String) user_map.get("gender");
        if (StringUtils.isNotBlank(gender) && gender.equals("f")) {
            gen = 2;
        } else if (StringUtils.isNotBlank(gender) && gender.equals("n")) {
            gen = 0;
        }
        umsMember.setGender(gen);
        umsMember.setCity((String) user_map.get("location"));
        umsMember.setCreateTime(new Date((String) user_map.get("created_at")));
        umsMember.setSourceUid(Long.valueOf((String) user_map.get("idstr")));
        umsMember.setSourceType(2);
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);


        //若数据库中不存在此新浪微博用户才进行插入数据库操作
        UmsMember checkUser = new UmsMember();
        Long sourceUid = umsMember.getSourceUid();
        checkUser.setSourceUid(sourceUid);

        UmsMember umsMember1 = userService.checkAuthUser(checkUser);

        if (umsMember1 == null) {
            umsMember = userService.addAuthUserToDb(umsMember);  //RPC的主键返回策略失效
        } else {
            umsMember = umsMember1;
        }

        //5.使用jwt生成token，将其传到首页中的cookie中

        UseJwtMakeToken useJwtMakeToken = new UseJwtMakeToken(request, umsMember).invoke();
        String token = useJwtMakeToken.getToken();

        return "redirect:http://search.mall.com:8083/index?token=" + token;
    }


    /**
     * 目的只是校验token
     *
     * @param token
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {

        //通过jwt校验token的真假

        Map<String, Object> map = new HashMap<>();

        //通过jwt制作token
        //jwt服务器部分,通过md5算法加密
        String key = "MuNanMall";
        for (int i = 0; i < 621; i++) {

            key = MD5Encode(key, "utf-8");
        }

        String salt = currentIp;
        for (int i = 0; i < 621; i++) {

            salt = MD5Utils.MD5Encode(salt, "utf-8");
        }

        Map<String, Object> decode = JwtUtil.decode(token, key, salt);

        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", decode.get("memberId"));
            map.put("memberNickname", decode.get("memberNickname"));
        } else {
            map.put("status", "fail");
        }


        return JSON.toJSONString(map);
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
            UseJwtMakeToken useJwtMakeToken = new UseJwtMakeToken(request, umsMember1).invoke();
            token = useJwtMakeToken.getToken();

        } else {
            //登录失败
            token = "fail";
        }

        return token;
    }


    @RequestMapping("index") //@RequestParam("returnUrl")
    public String index(String returnUrl, ModelMap modelMap) {

        if (StringUtils.isNotBlank(returnUrl)) {
            modelMap.put("returnUrl", returnUrl);
        }

        return "index";
    }

    private class UseJwtMakeToken {
        private HttpServletRequest request;
        private UmsMember umsMember1;
        private String token;
        private String memberId;

        public UseJwtMakeToken(HttpServletRequest request, UmsMember umsMember1) {
            this.request = request;
            this.umsMember1 = umsMember1;
        }

        public String getToken() {
            return token;
        }

        public String getMemberId() {
            return memberId;
        }

        public UseJwtMakeToken invoke() {
            //通过jwt制作token
            //jwt服务器部分,通过md5算法加密
            String key = "MuNanMall";
            for (int i = 0; i < 621; i++) {

                key = MD5Encode(key, "utf-8");
            }

            //jwt的私有部分，是用户信息
            Map<String, Object> map = new HashMap<>();

            memberId = umsMember1.getId();
            String userNickname = umsMember1.getNickname();
            map.put("memberId", memberId);  //是保存数据库后主键返回策略生成的id
            map.put("memberNickname", userNickname);

            //jwt浏览器客户端部分(盐值)，通过md5算法加密
            //浏览器部分
            String ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            String salt = ip;

            for (int i = 0; i < 621; i++) {

                salt = MD5Utils.MD5Encode(salt, "utf-8");
            }//380adba  44fa

            //通过jwt获取token
            token = JwtUtil.encode(key, map, salt);


            //将token存入redis一份
            userService.addUserTokenToCache(token, memberId);
            return this;
        }
    }
}
