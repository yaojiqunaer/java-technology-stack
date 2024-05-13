package io.github.yaojiqunaer.consumer;

import io.github.yaojiqunaer.service.DownStreamService;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static io.github.yaojiqunaer.common.Const.STRING_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * kafka 消费者测试
 * 1. @EmbeddedKafka 注解会在测试类启动时自动创建一个嵌入式的 Kafka 服务
 * 1.1 partitions 指定分区数
 * 1.2 topics 创建主题
 * 1.3 brokerProperties 指定 broker 属性
 * 2. @SpringBootTest 注解会在测试类启动时自动创建一个 SpringBoot 应用
 * 3. @DirtiesContext 注解会在每个测试方法执行后重置 SpringBoot 应用
 * 4. @Resource 注解会自动注入 KafkaTemplate
 * 5. @BeforeEach 注解会在每个测试方法执行前执行
 * 6. @AfterEach 注解会在每个测试方法执行后执行
 */
@SpringBootTest(classes = {io.github.yaojiqunaer.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@EmbeddedKafka(
        partitions = 2,
        topics = {STRING_TOPIC},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class StringKafkaConsumersTest {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @SpyBean
    private StringKafkaConsumers stringKafkaConsumers;
    @MockBean
    private DownStreamService downStreamService;

    @Test
    void handleMessage() {
        // mock值
        ConsumerRecord<String, Object> record =
                new ConsumerRecord<>("topic", 0, 0L, "key", "value");
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        when(downStreamService.mockDownStream(any())).thenReturn(true);
        stringKafkaConsumers.handleMessage(record, acknowledgment);
        Mockito.verify(downStreamService).mockDownStream(any());
    }
}