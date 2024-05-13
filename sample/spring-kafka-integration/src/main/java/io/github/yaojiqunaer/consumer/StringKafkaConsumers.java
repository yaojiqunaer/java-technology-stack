package io.github.yaojiqunaer.consumer;

import io.github.yaojiqunaer.service.DownStreamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static io.github.yaojiqunaer.common.Const.STRING_TOPIC;

@Slf4j
@Component
public class StringKafkaConsumers {

    @Autowired
    private DownStreamService downStreamService;

    @KafkaListener(topics = STRING_TOPIC, groupId = "${spring.kafka.consumer.group-id:group-id-test}")
    public void handleMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        try {
            String message = (String) record.value();
            log.info("收到消息: {}", message);
            Boolean success = downStreamService.mockDownStream(false);
            if (!success) {
                throw new RuntimeException("模拟下游服务异常");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }

}
