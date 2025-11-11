package io.github.yaojiqunaer.producer;

import io.github.yaojiqunaer.avro.User;
import io.github.yaojiqunaer.exception.ApiResponse;
import io.github.yaojiqunaer.exception.BaseApiResponse;
import jakarta.annotation.Resource;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static io.github.yaojiqunaer.common.Const.AVRO_TOPIC;

@RestController
@RequestMapping("/avro")
public class AvroKafkaProducers {

    @Resource
    private KafkaTemplate<String, SpecificRecordBase> avroKafkaTemplate;

    @GetMapping("/send")
    public ApiResponse send(@NotNull @RequestParam String username) {
        User user = new User();
        user.setId(RandomUtils.nextLong());
        user.setAge(RandomUtils.nextInt(0,120));
        user.setDate(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        user.setUsername(username);
        avroKafkaTemplate.send(AVRO_TOPIC, user);
        return BaseApiResponse.ok();
    }



}
