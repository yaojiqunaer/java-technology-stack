package io.github.yaojiqunaer.resttemplate.util;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class ClientHttpRequestFactoryUtils {

    /**
     * generate SimpleClientHttpRequestFactory
     *
     * @param connectTimeout millis
     * @param readTimeout    millis
     * @return SimpleClientHttpRequestFactory
     */
    public static ClientHttpRequestFactory simpleClientHttpRequestFactory(int connectTimeout,
                                                                          int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    /**
     * @param maxTotal
     * @param defaultMaxPerRoute
     * @return
     */
    public static HttpClientConnectionManager poolingHttpClientConnectionManager(int maxTotal,
                                                                                 int defaultMaxPerRoute) {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(maxTotal)
                .setMaxConnPerRoute(defaultMaxPerRoute)
                .build();
    }


}
