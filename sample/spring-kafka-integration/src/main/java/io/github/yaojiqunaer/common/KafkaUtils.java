package io.github.yaojiqunaer.common;

import io.github.yaojiqunaer.commons.lang.ExceptionUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

public class KafkaUtils {

    private KafkaUtils() {
        ExceptionUtils.throwUtilConstructor();
    }

    public static <K, V extends Serializable> CompletableFuture<SendResult<K, V>> send(KafkaTemplate<K, V> kafkaTemplate,
                                                                                       String topic, V message) {
        return kafkaTemplate.send(topic, message);
    }

}
