package io.github.yaojiqunaer.resttemplate;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class RestTemplateLogInterceptor implements ClientHttpRequestInterceptor {

    @NotNull
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        Map<String, String> map = new HashMap<>();
        map.put("地址", request.getURI().toString());
        map.put("类型", request.getMethod().toString());
        map.put("请求头", String.valueOf(request.getHeaders()));
        map.put("参数", new String(body, StandardCharsets.UTF_8));
        log.info("REST请求开始: {}", map);
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("响应状态码", response.getStatusCode().value());
//        map.put("RESPONSE_BODY","略"); //不开启响应数据打印 防止定时器http调用打印过大的响应数据
        log.info("REST请求结束: {}", map);
    }

}