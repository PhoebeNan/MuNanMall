package com.zyn.mall.interceptors;

import com.alibaba.fastjson.JSON;
import com.zyn.mall.annotations.LoginRequired;
import com.zyn.mall.util.HttpclientUtil;
import com.zyn.mall.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-12-05-14:19
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    //Object handler  表示http请求的方法
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //拦截代码


        //判断被拦截请求方法的注解，先通过反射获取被请求方法的注解(有此注解则需要被拦截)
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);

        StringBuffer url = request.getRequestURL();
        System.out.println(url.toString());
        //是否拦截
        if (loginRequired == null) {
            return true; //没有注解@LoginRequired,拦截器不拦截直接放行，可以执行下面的方法
        }


        //老token不为空，新token为空，表示之前登录过
        //新token不为空，老token为空，表示刚刚登录
        //新，老token都为空，表示从未登录
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);

        String newToken = request.getParameter("token");

        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        //若新老token都不为空的话，说明token过期了，将从认证中心颁发的token覆盖老token
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }


        //会拦截但是会判断注解的状态，是否必须登录  true代表必须验证其用户身份
        boolean loginSuccess = loginRequired.loginSuccess();

        //调用认证中心进行验证
        String success = "failx";
        Map<String, String> jsonMap = new HashMap<>();
        if (StringUtils.isNotBlank(token)) {

            //jwt浏览器客户端部分(盐值)，通过md5算法加密
            //浏览器部分
            String ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }

            //目的是验证token中的用户身份信息是否有效
            String successJson = HttpclientUtil.doGet("http://passport.mall.com:8085/verify?token=" + token + "&currentIp=" + ip);
            jsonMap = JSON.parseObject(successJson, Map.class);

            success = jsonMap.get("status");

        }


        if (loginSuccess) {
            //必须登录才能执行注解下面的方法

            if (!success.equals("success")) {
                //如果验证失败，重定向到passport登录,并携带你请求时的地址

                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://passport.mall.com:8085/index?returnUrl=" + requestURL);

                return false;
            }
            //验证通过，合法，覆盖cookie中的token
            //需要将token携带的用户信息写入
            request.setAttribute("memberId", jsonMap.get("memberId"));
            request.setAttribute("memberNickname", jsonMap.get("memberNickname"));

            //验证通过，覆盖cookie中的token
            if (StringUtils.isNotBlank(token)) {
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
            }

        } else {
            //虽被拦截，但是不用登录也可以执行方法，但是必须验证，通过了给用户id，没通过也能放行
            if (success.equals("success")) {
                //不管验证是否成功,都可以通过，需要将token携带的用户信息写入request域中
                request.setAttribute("memberId", jsonMap.get("memberId"));
                request.setAttribute("memberNickname", jsonMap.get("memberNickname"));

                //验证通过，覆盖cookie中的token
                if (StringUtils.isNotBlank(token)) {
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }
            }

        }


        return true;  //true代表拦截器放行，继续执行程序
    }
}
