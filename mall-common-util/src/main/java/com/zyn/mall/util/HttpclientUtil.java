package com.zyn.mall.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-11-13-8:21
 */
public class HttpclientUtil {

    public static String doGet(String url) throws Exception {


        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();

        //创建httpClient
        CloseableHttpClient httpclient = HttpClients.custom().setSSLContext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();


        // 创建Httpclient对象
//        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求

        HttpGet httpGet = new HttpGet(url);
//        SslUtils.ignoreSsl();

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


    public static String doPost(String url, Map<String, String> paramMap) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http Post请求
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            List<BasicNameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            HttpEntity httpEntity = new UrlEncodedFormEntity(list, "utf-8");

            httpPost.setEntity(httpEntity);
            // 执行请求
            response = httpclient.execute(httpPost);

            return statusOK(response, httpclient);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String statusOK(CloseableHttpResponse response, CloseableHttpClient httpclient) throws IOException {

        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
            httpclient.close();
            return result;
        }
        httpclient.close();

        return null;
    }

}
