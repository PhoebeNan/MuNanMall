package com.zyn.mall.util;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-11-13-8:21
 */
public class HttpclientUtil {

    public static List<String> doGet(String url) throws Exception {


        CloseableHttpClient httpclient = getCloseableHttpClientSSL();


//        //打包将要传入的参数
//        List<NameValuePair> list = new ArrayList<>();
//        list.add(new BasicNameValuePair("username", name));
//        list.add(new BasicNameValuePair("password",password));
//
//        //3、转化参数
//        String params = EntityUtils.toString(new UrlEncodedFormEntity(list, Consts.UTF_8));

        // 创建Httpclient对象
//        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求

        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {


            // 执行请求
            response = httpclient.execute(httpGet);

            return statusOK(response, httpclient);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //进行SSL认证，可以使用Java程序访问https资源
    private static CloseableHttpClient getCloseableHttpClientSSL() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());

        return HttpClients.custom().setSSLSocketFactory(
                sslsf).build();
    }


    public static List<String> doPost(String url, Map<String, String> paramMap) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建Httpclient对象
        CloseableHttpClient httpclient = getCloseableHttpClientSSL();
        // 创建http Post请求
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            if (paramMap != null) {
                List<BasicNameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(list, "utf-8");

                httpPost.setEntity(httpEntity);
            }
            // 执行请求
            response = httpclient.execute(httpPost);

            return statusOK(response, httpclient);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static List<String> statusOK(CloseableHttpResponse response, CloseableHttpClient httpclient) throws IOException {

        List<String> res = new ArrayList<>();

        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();


            String redirectUrl = "";
            Header[] headers = response.getHeaders("location");
            if(headers!=null&&headers.length>0){
                redirectUrl = headers[0].getValue();

                //System.out.println("响应后重定向的地址为："+redirectUrl);
            }

            String result = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);

            res.add(result);
            res.add(redirectUrl);

            httpclient.close();
            return res;
        }


        httpclient.close();

        return null;
    }


}
