package io.github.yaojiqunaer.producer;

import io.github.yaojiqunaer.common.KafkaUtils;
import io.github.yaojiqunaer.exception.ApiResponse;
import io.github.yaojiqunaer.exception.BaseApiResponse;
import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import java.util.concurrent.CompletableFuture;

import static io.github.yaojiqunaer.common.Const.STRING_TOPIC;

@RestController
@RequestMapping("/string")
public class StringKafkaProducers {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/send")
    public ApiResponse send(@NotNull @RequestParam String username) {
        String user = """
                    {
                      "type": "record",
                      "name": "User",
                      "namespace": "io.github.yaojiqunaer.avro",
                      "fields": [
                        {
                          "name": "id",
                          "type": "long"
                        },
                        {
                          "name": "username",
                          "type": "string"
                        },
                        {
                          "name": "age",
                          "type": "int"
                        },
                        {
                          "name": "date",
                          "type": "string"
                        }
                      ]
                    }
                """;
        kafkaTemplate.send(STRING_TOPIC, user);
        return BaseApiResponse.ok();
    }


}
