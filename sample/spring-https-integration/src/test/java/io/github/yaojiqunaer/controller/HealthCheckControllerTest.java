/*
package io.github.yaojiqunaer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

@SpringBootTest
class HealthCheckControllerTest {

    private final static String TEST_URL = "https://127.0.0.1/ping";

    @Test
    public void getHKVesselTrip() throws Exception {
        // 客户端证书类型
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        // 加载客户端证书，即自己的私钥
        clientStore
                .load(new FileInputStream("/Users/xiaodongzhang/test/client-keystore.p12"),
                        "changeit".toCharArray());
        // 创建密钥管理工厂实例
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // 初始化客户端密钥库
        kmf.init(clientStore, "changeit".toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();
        // 创建信任库管理工厂实例
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // 信任库类型
        KeyStore trustStore = KeyStore.getInstance("JKS");
        // 加载信任库，即服务端公钥
        trustStore.load(new FileInputStream("D:\\jdk\\jre\\lib\\security\\cacerts"),
                "changeit".toCharArray());
        // 初始化信任库
        tmf.init(trustStore);
        TrustManager[] tms = tmf.getTrustManagers();
        // 建立TLS连接
        SSLContext sslContext = SSLContext.getInstance("TLS");
        // 初始化SSLContext
        sslContext.init(kms, tms, new SecureRandom());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpGet httpget = new HttpGet(TEST_URL);
            System.out.println("executing request" + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println(EntityUtils.toString(entity));
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}*/
