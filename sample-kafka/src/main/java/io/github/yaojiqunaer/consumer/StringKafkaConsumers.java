package io.github.yaojiqunaer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static io.github.yaojiqunaer.common.Const.STRING_TOPIC;

@Slf4j
@Component
public class StringKafkaConsumers {

    @KafkaListener(topics = STRING_TOPIC, groupId = "${spring.kafka.consumer.group-id:test-group}")
    public void handleMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        try {
            String message = (String) record.value();
            log.info("收到消息: {}", message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }

}
