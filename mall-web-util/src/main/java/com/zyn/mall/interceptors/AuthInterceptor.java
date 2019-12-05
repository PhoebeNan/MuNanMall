package com.zyn.mall.interceptors;

import com.zyn.mall.annotations.LoginRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        HandlerMethod  handlerMethod = (HandlerMethod)handler;
        LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);


        return true;
    }
}
