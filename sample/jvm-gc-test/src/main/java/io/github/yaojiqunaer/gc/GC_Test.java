package io.github.yaojiqunaer.gc;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class GC_Test {

    public static void main(String[] args) throws InterruptedException {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(1000)
                .setMaxConnPerRoute(1000)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient)));
        List list = new ArrayList();
        for (int i = 0; i < 100000; i++) {
            String forObject = restTemplate.getForObject("http://127.0.0.1:443/ping", String.class);
            Thread.sleep(10);
            String test = restTemplate.getForObject("http://localhost:443/ping", String.class);
            for (int j = 0; j < 1000; j++) {
                list.add(test);
            }
        }
    }


}
