package io.github.yaojiqunaer.resttemplate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.component.rest-template")
public class RestTemplateProperties {

    /**
     * Rest策略配置
     */
    private Strategy strategy = new Strategy();

    /**
     * Http连接池配置
     */
    private HttpConnPool httpConnPool = new HttpConnPool();

    @Data
    static class Strategy {

        /**
         * 是否跟随3XX重定向，RestTemplate默认不会
         */
        private Boolean followRedirect = false;
        /**
         * SSL校验(HTTPS)是否开启，默认true
         */
        private SSL ssl;
    }

    @Data
    static class SSL {
        private Boolean enabled = false;
    }

    @Data
    static class HttpConnPool {
        /**
         * Http连接工厂
         */
        private String factoryClassName = SimpleClientHttpRequestFactory.class.getCanonicalName();

        /**
         * Http连接池最大连接数
         */
        private Integer maxTotal = 50;
        /**
         * 同一路由并行数量
         */
        private Integer defaultMaxPerRoute = 30;
        /**
         * 连接上服务器(握手成功)的时间(毫秒)，超出抛出connect timeout
         */
        private Integer connectTimeout = 10_000;
        /**
         * 从连接池中获取连接的超时时间，超时间未拿到可用连接，会抛出{@link org.apache.hc.core5.http.ConnectionRequestTimeoutException
         * ConnectionRequestTimeoutException}
         */
        private Integer connectionRequestTimeout = 5_000;
        /**
         * 服务器返回数据(response)的时间，超过抛出read timeout
         */
        private Integer responseTimeout = 65_000;
        /**
         * 空闲永久连接检查间隔
         */
        private Integer validateAfterInactivity = 2000;

    }


}
