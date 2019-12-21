package com.zyn.mall.order.service.impl;

public class Test01 {//新建一个类
    String s = new String("good");//创建一个对象名字为s内容为good
    String[] ss = {"aaa"};//创建一个名为ss的数组只有1个数量内容为aaa

    public void m_method(String str, String[] sa) {//设置一个公共的无返回值的名为m_method的函数 （）里面是参数
        str = "bad";//把bad赋值给str
        sa[0] = "bbb";
        //把bbb赋值给sa的第一个数组对象
    }

    public static void main(String[] args) {//程序入口
        Test01 t1 = new Test01();//在Test01里创建一个名为t1的对象
        t1.m_method(t1.s, t1.ss);//对象t1调用test01的m_method函数，t1.s也就是test01类的s也就是good作为第一个参数，t1.ss也就是test01类里面的ss也就是aaa作为第二个参数进行运行
        System.out.println(t1.s + t1.ss[0]); //输出t1.s的值和t1.ss[0]的值；
    }
}