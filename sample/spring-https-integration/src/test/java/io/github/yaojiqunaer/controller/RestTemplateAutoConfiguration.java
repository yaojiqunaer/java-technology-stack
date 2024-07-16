package io.github.yaojiqunaer.controller;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class RestTemplateAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateAutoConfiguration.class);

    static RestTemplate getApacheTemplate() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream in = new FileInputStream("path/to/your/truststore.jks")) {
            keyStore.load(in, "changeit".toCharArray());
        }

        // 创建 socketFactoryRegistry
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(keyStore, null).build();
        ConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
                .register(URIScheme.HTTPS.id, connectionSocketFactory)
                .build();


        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                // 连接建立的超时时间,单位毫秒
                .setConnectTimeout(Timeout.ofSeconds(30))
                // 套接字读写操作的超时时间,单位毫秒
                .setSocketTimeout(Timeout.ofSeconds(30))
                // 连接的最大生存时间,单位秒
                .setTimeToLive(Timeout.ofSeconds(30))
                // 连接空闲多长时间后需要进行可用性检查,单位秒
                .setValidateAfterInactivity(Timeout.ofSeconds(90))
                .build();


        SocketConfig socketConfig = SocketConfig.custom()
                // 设置 Socket 读写操作的超时时间为 30 秒
                .setSoTimeout(Timeout.ofSeconds(30))
                // 禁止地址复用
                .setSoReuseAddress(false)
                // 设置 Socket 保持连接的时长为 10 秒
                .setSoLinger(TimeValue.ofSeconds(10))
                // 开启 keep-alive
                .setSoKeepAlive(true)
                // 关闭 TCP 无延迟模式
                .setTcpNoDelay(false)
                // 设置发送缓冲区大小为 8192 字节
                .setSndBufSize(8192)
                // 设置接收缓冲区大小为 8192 字节
                .setRcvBufSize(8192)
                // 设置连接请求队列的最大长度为 100
                .setBacklogSize(100)
                // 设置 SOCKS 代理地址为 proxy.example.com:1080
                //.setSocksProxyAddress(new InetSocketAddress("proxy.example.com", 1080))
                .build();

        TlsConfig tlsConfig = new TlsConfig.Builder()
                // 设置握手超时时间为 30 秒
                .setHandshakeTimeout(Timeout.ofSeconds(30))
                // 支持 TLSv1.2 和 TLSv1.3 两个 TLS 协议版本
                .setSupportedProtocols(TLS.V_1_0, TLS.V_1_1, TLS.V_1_2, TLS.V_1_3)
                // 支持以下加密套件
                /*.setSupportedCipherSuites(
                        "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                        "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                        "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"
                )*/
                // 强制使用 HTTP/1.1 协议
                // .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
                .build();
        BasicHttpClientConnectionManager connectionManager =
                new BasicHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setConnectionConfig(connectionConfig);
        connectionManager.setSocketConfig(socketConfig);
        connectionManager.setTlsConfig(tlsConfig);

        HttpClient client = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .evictExpiredConnections()
                .build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

    }

    static RestTemplate getJDK() throws Exception {

        JdkClientHttpRequestFactory jdkClientHttpRequestFactory = new JdkClientHttpRequestFactory();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(@NotNull HttpURLConnection connection, @NotNull String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
            }
        };
        return new RestTemplate(jdkClientHttpRequestFactory);
    }

    static RestTemplate getOkhttpTemplate() throws Exception {
        // 配置 SSL 上下文和 TLS 版本（如果需要）
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        trustManagerFactory.init(trustStore);
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        // 创建 OkHttpClient 实例并配置
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), new X509TrustManager() {

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                })
                .connectionPool(new ConnectionPool(5, 10, TimeUnit.MINUTES)) // 设置连接池大小和空闲连接存活时间
                // 其他配置，如超时、拦截器等
                .build();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
        return new RestTemplate(factory);
    }


    public static void main(String[] args) throws InterruptedException {
        String url = "https://aiahk-apigw3-uat.aia.com.hk/gateway/digital.customer-idirect-ec/v1.0/insuredECards";
        String jwt = """
                """;
        HttpHeaders headers  = new HttpHeaders();
        headers.add("Authorization", jwt);
        headers.add("x-AIAHK-Trace-ID", UUID.randomUUID().toString());
        headers.add("x-AIAHK-Context-ID", UUID.randomUUID().toString());

        for (int i = 0; i < 100; i++) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                @Override
                public void handleError(@NotNull ClientHttpResponse response) throws IOException {
                   log.error("http code: {}", response.getStatusCode().value());
                }
            });
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            Thread.sleep(2000);
        }

/*        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @SneakyThrows
            @Override
            protected void prepareConnection(@NotNull HttpURLConnection connection, @NotNull String httpMethod)
            throws IOException {
                super.prepareConnection(connection, httpMethod);
                if (connection instanceof HttpsURLConnection httpsConnection) {
                    httpsConnection.setHostnameVerifier((hostname, session) -> true);
                    httpsConnection.setSSLSocketFactory(trustAllSSLContext().getSocketFactory());
                }
            }
        };
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ResponseEntity<String> exchange = restTemplate.exchange("https://localhost/post", HttpMethod.POST, new
        HttpEntity<>(new HashMap<>()),
                String.class);
        System.out.println(exchange.toString());*/
    }

    static SSLContext trustAllSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());
        return sslContext;
    }


}
