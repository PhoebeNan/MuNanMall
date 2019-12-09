package com.zyn.mall.PKIX;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class SslTest {

    public static String getRequest(String url, int timeOut) throws Exception {
        URL u = new URL(url);
        if ("https".equalsIgnoreCase(u.getProtocol())) {
            SslUtils.ignoreSsl();
        }
        URLConnection conn = u.openConnection();
        conn.setConnectTimeout(timeOut);
        conn.setReadTimeout(timeOut);
        return conn.getInputStream().toString();
    }

    public static String postRequest(String urlAddress, String args, int timeOut) throws Exception {
        URL url = new URL(urlAddress);
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            SslUtils.ignoreSsl();
        }
        URLConnection u = url.openConnection();
        u.setDoInput(true);
        u.setDoOutput(true);
        u.setConnectTimeout(timeOut);
        u.setReadTimeout(timeOut);
        OutputStreamWriter osw = new OutputStreamWriter(u.getOutputStream(), "UTF-8");
        osw.write(args);
        osw.flush();
        osw.close();
        u.getOutputStream();
        return u.getInputStream().toString();
    }

    public static void main(String[] args) {
        try {
            SslTest st = new SslTest();
            String a = st.getRequest("https://xxx.com/login.action", 3000);
            System.out.println(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}