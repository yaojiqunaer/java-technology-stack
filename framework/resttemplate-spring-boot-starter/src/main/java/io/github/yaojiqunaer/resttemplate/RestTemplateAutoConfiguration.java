package io.github.yaojiqunaer.resttemplate;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


@Slf4j
@AutoConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties(RestTemplateProperties.class)
public class RestTemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RestTemplateLogInterceptor.class)
    public RestTemplateLogInterceptor restTemplateLogInterceptor() {
        return new RestTemplateLogInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory clientHttpRequestFactory(RestTemplateProperties restTemplateProperties) {
        return new HttpComponentsClientHttpRequestFactory(generateHttpClient(restTemplateProperties));
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(RestTemplate.class)
    @ConditionalOnBean({ClientHttpRequestFactory.class, RestTemplateLogInterceptor.class})
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory,
                                     RestTemplateLogInterceptor restTemplateLogInterceptor) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        //拦截器 默认开启日志打印
        restTemplate.setInterceptors(Collections.singletonList(restTemplateLogInterceptor));
        // 解决中文乱码问题
        // restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    public static HttpClient generateHttpClient(RestTemplateProperties restTemplateProperties) {
        RestTemplateProperties.HttpConnPool httpConnPool = restTemplateProperties.getHttpConnPool();
        RestTemplateProperties.Strategy strategy = restTemplateProperties.getStrategy();
        // 是否校验SSL
        PoolingHttpClientConnectionManager connectionManager = strategy.getSslVerify() ?
                new PoolingHttpClientConnectionManager() : new PoolingHttpClientConnectionManager(initRegistry());
        //连接池最大连接数
        connectionManager.setMaxTotal(httpConnPool.getMaxTotal());
        //同路由最大并行数
        connectionManager.setDefaultMaxPerRoute(httpConnPool.getDefaultMaxPerRoute());
        //空闲连接检查间隔
        connectionManager.setValidateAfterInactivity(TimeValue.of(httpConnPool.getValidateAfterInactivity(),
                MILLISECONDS));
        RequestConfig requestConfig = RequestConfig.custom()
                //响应超时
                .setResponseTimeout(Timeout.ofMilliseconds(httpConnPool.getResponseTimeout()))
                //自动重定向
                .setRedirectsEnabled(strategy.getFollowRedirect())
                //连接超时
                .setConnectTimeout(Timeout.ofMilliseconds(httpConnPool.getConnectTimeout()))
                //连接上连接池超时
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(httpConnPool.getConnectionRequestTimeout()))
                .build();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        if (strategy.getFollowRedirect()) {
            // 跟随重定向策略
            httpClientBuilder.setRedirectStrategy(new DefaultRedirectStrategy());
        }
        httpClientBuilder.setConnectionManager(connectionManager);
        return httpClientBuilder.build();
    }

    @NotNull
    private static Registry<ConnectionSocketFactory> initRegistry() {
        RegistryBuilder<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory());
        SSLContext sslContext = getDisabledSSLContext();
        registry.register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE));
        return registry.build();
    }

    private static SSLContext getDisabledSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new DisabledValidationTrustManager()}, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.warn("Error creating SSLContext", e);
        }
        return sslContext;
    }


    /**
     * ssl校验忽掠
     */
    public static class DisabledValidationTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }


}
