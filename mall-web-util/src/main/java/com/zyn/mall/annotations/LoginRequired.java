package com.zyn.mall.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhaoyanan
 * @create 2019-12-05-14:53
 */
@Target(ElementType.METHOD) //在方法上有效
@Retention(RetentionPolicy.RUNTIME) //在虚拟机运行期间依然有效，注解的生命周期
public @interface LoginRequired {  //没有此注解就不拦截，可以成功访问方法

    boolean loginSuccess() default true;//表示必须用户登录才能访问，false表示用户没登录也可以访问，但是也会拦截
}
