package com.zyn.mall.utils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-20-11:01
 */
public class TokenUtil {

    private final static String TOKEN_LIST_NAME = "tokenList";
    private final static String TOKEN_STRING_NAME = "tokenName";

    /**
     * 获取token的value ，
     * @param session
     * @return   tokenStr已经存储到一个List集合中了，而list集合已经存储到了session域中了
     */
    public static String getTokenString(HttpSession session){
        String tokenStr = generateTokenString();
        saveTokenString(tokenStr,session);
        return tokenStr;
    }

    /**
     * 把token保存到List集合中，此时session中已经存在tokenList集合了
     * @param tokenStr
     * @param session
     */
    private static void saveTokenString(String tokenStr, HttpSession session) {
        List list =  getTokenList(session);
        list.add(tokenStr);
    }

    /**
     * 把list集合添加到sesion中
     * @param session
     * @return
     */
    private static List getTokenList(HttpSession session) {
        Object tokenList = session.getAttribute(TOKEN_LIST_NAME);
        if (tokenList != null){
            return (List) tokenList;
        }else {
            //session中没有tokenList列表，则进行添加操作
            List tokenLists = new ArrayList();
            session.setAttribute(TOKEN_LIST_NAME,tokenLists);
            return tokenLists;
        }
    }

    /**
     * 生成一个token随机值
     * @return
     */
    private static String generateTokenString() {

        return new Long(System.currentTimeMillis()).toString();
    }

    /**
     * 判断token是否有效，若有效，则返回true
     * @param tokenStr
     * @param session
     * @return
     */
    public static boolean isTokenStringValid(String tokenStr, HttpSession session){
        boolean valid = false;
        if(session!=null){
            List tokenList = getTokenList(session);
            if ((tokenList.contains(tokenStr))){
                valid = true;
                tokenList.remove(tokenStr);
            }
        }
        return valid;
    }
}
