package io.github.yaojiqunaer.consumer;

import io.github.yaojiqunaer.avro.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static io.github.yaojiqunaer.common.Const.AVRO_TOPIC;

@Slf4j
@Component
public class AvroKafkaConsumers {

    @KafkaListener(topics = AVRO_TOPIC, groupId = "${spring.kafka.consumer.group-id:group-id-test}", containerFactory = "avroKafkaListenerContainerFactory")
    public void handleMessage(ConsumerRecord<String, User> record, Acknowledgment acknowledgment) {
        try {
            User value = record.value();
            log.info("收到消息: {}", value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }

}
