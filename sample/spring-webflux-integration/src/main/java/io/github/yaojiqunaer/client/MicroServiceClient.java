package io.github.yaojiqunaer.client;

import io.github.yaojiqunaer.entity.PostDTO;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MicroServiceClient {

    // 该网站提供了免费rest api
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static final WebClient webClient = WebClient.create(BASE_URL);

    /**
     * 同步：接受单个数据用mono
     */
    public static void sendMonoBlock() {
        PostDTO block = webClient
                .get()
                .uri("/posts/1")
                .retrieve()
                .bodyToMono(PostDTO.class)
                .block();
        log.info("send mono block with base url, response: {}", block);
    }

    /**
     * 同步：接受批量数据用flux
     */
    public static void sendFluxBlock() {
        List<PostDTO> block = webClient
                .get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(PostDTO.class)
                .collectList()
                .block();
        log.info("send flux block with base url, response: {}", block);
    }

    /**
     * 响应式接收
     */
    @SneakyThrows
    public static void sendMonoSubscribe() {
        webClient
                .get()
                .uri("/posts/1")
                .retrieve()
                .bodyToMono(PostDTO.class)
                .subscribe(data -> {
                    // 这里的数据处理是异步的
                    log.info("send mono subscribe with base url, thread: {}, response: {}",
                            Thread.currentThread().getName(), data);
                });
        log.info("send mono subscribe with base url, thread: {}", Thread.currentThread().getName());
        Thread.sleep(1000L);
    }

    public static void sendExchange() {
        PostDTO block = webClient
                .get()
                .uri("/posts/1")
                .exchangeToMono(response -> {
                    log.info("send exchange with base url, response code: {}", response.statusCode().value());
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(PostDTO.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .block();
        log.info("send exchange with base url, response: {}", block);
    }

    public static void sendPathParam() {
        PostDTO block = webClient
                .get()
                .uri("/{1}/{2}", "posts", "1")
                .retrieve()
                .bodyToMono(PostDTO.class)
                .block();
        log.info("send path param with base url, response: {}", block);
    }

    public static void sendPostStringBody() {
        String body = """
                {
                    "userId": 1,
                    "title": "demo title",
                    "body": "demo body"
                }
                """;
        String block = webClient
                .post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("send string body with base url, response: {}", block);
    }

    public static void sendPostObjectBody() {
        PostDTO body = new PostDTO();
        body.setUserId(1);
        body.setTitle("demo title");
        body.setBody("demo body");
        PostDTO block = webClient
                .post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PostDTO.class)
                .block();
        log.info("send java obj body with base url, response: {}", block);
    }

    public static void sendDelete() {
        webClient
                .delete()
                .uri("/posts/1")
                .exchangeToMono(response -> {
                    log.info("send delete with base url, response code: {}", response.statusCode().value());
                    return response.bodyToMono(String.class);
                })
                .block();
    }

    public static void sendPut() {
        PostDTO body = new PostDTO();
        body.setUserId(1);
        body.setTitle("demo title2");
        body.setBody("demo body2");
        PostDTO block = webClient
                .put()
                .uri("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PostDTO.class)
                .block();
        log.info("send put with base url, response: {}", block);
    }

    public static WebClient configClient(int connectionTimeout) {
        HttpClient httpClient = HttpClient.create()
                .baseUrl(BASE_URL)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public static void sendWithConnectTimoutException() {
        WebClient webClient = configClient(5);
        String block = webClient
                .get()
                .uri("/posts/1")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientRequestException.class, err -> log.error("send with connect timeout exception with base url, error: {}", err.getMessage()))
                .onErrorReturn("error")
                .block();
        System.out.println(block);
    }

    public static void sendHandleStatusCode() {
        String block = webClient
                .get()
                .uri("/notFound")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("not found: " + response.statusCode().value())))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("server error: " + response.statusCode().value())))
                .bodyToMono(String.class)
                .doOnError(RuntimeException.class, err -> log.error("send handle status code with base url, error: {}", err.getMessage()))
                .onErrorReturn(RuntimeException.class, "runtime exception")
                .block();
        System.out.println(block);
    }

    public static void sendRetry() {
        String block = webClient
                .get()
                .uri("/notFound")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(Exception.class, err -> log.error("send retry with base url, time: {}", LocalDateTime.now()))
                .onErrorReturn("retry")
                .retry(3)
                .block();
        System.out.println(block);
    }


    public static void main(String[] args) {
        sendMonoBlock();
        sendFluxBlock();
        sendMonoSubscribe();
        sendExchange();
        sendPathParam();
        sendPostStringBody();
        sendPostObjectBody();
        sendDelete();
        sendPut();

        configClient(5000);
        sendWithConnectTimoutException();
        sendHandleStatusCode();

        sendRetry();

    }


}
