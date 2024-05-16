package io.github.yaojiqunaer.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisTemplateDemo {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    private static final String KEY = "key";
    private static final String VALUE = "value";

    @PostConstruct
    public void init() {
        setString();
        getString();
        deleteKey();

        setHash();
        getHash();
        deleteHash();

        // 队列
        setList();
        getList();

        // 无序不重复
        addSet();
        removeSet();
    }

    private void setString() {
        redisTemplate.opsForValue().set(KEY, VALUE, 60, TimeUnit.SECONDS);
        log.info("set string key successfully.");
    }

    private void getString() {
        Object value = redisTemplate.opsForValue().get(KEY);
        if (Objects.equals(VALUE, value)) {
            log.info("get string key successfully.");
        }
    }

    private void deleteKey() {
        Long delete = redisTemplate.delete(List.of(KEY, KEY + "1"));
        if (Objects.equals(delete, 1L)) {
            log.info("delete string key successfully.");
        }
    }

    private void setHash() {
        redisTemplate.opsForHash().put(KEY, "hashKey", "hashValue");
        redisTemplate.opsForHash().put(KEY, "hashKey1", "hashValue1");
        redisTemplate.expire(KEY, 60, TimeUnit.SECONDS);
        log.info("set hash key successfully.");
    }

    private void getHash() {
        Object hashValue = redisTemplate.opsForHash().get(KEY, "hashKey");
        if (Objects.equals("hashValue", hashValue)) {
            log.info("get hash key successfully.");
        }
        Object hashValue1 = redisTemplate.opsForHash().get(KEY, "hashKey1");
        if (Objects.equals("hashValue1", hashValue1)) {
            log.info("get hash key successfully.");
        }
    }

    private void deleteHash() {
        Long delete = redisTemplate.opsForHash().delete(KEY, "hashKey", "hashKey1");
        if (Objects.equals(delete, 2L)) {
            log.info("delete hash key successfully.");
        }
    }

    private void setList() {
        redisTemplate.opsForList().leftPush(KEY, "listValue");
        redisTemplate.opsForList().leftPush(KEY, "listValue1");
        redisTemplate.expire(KEY, 60, TimeUnit.SECONDS);
        log.info("set list key successfully.");
    }

    private void getList() {
        Object listValue = redisTemplate.opsForList().leftPop(KEY);
        if (Objects.equals("listValue1", listValue)) {
            log.info("get list key successfully.");
        }
        Object listValue1 = redisTemplate.opsForList().leftPop(KEY);
        if (Objects.equals("listValue", listValue1)) {
            log.info("get list key successfully.");
        }
    }

    private void addSet() {
        redisTemplate.opsForSet().add("setKey", "setValue");
        redisTemplate.opsForSet().add("setKey", "setValue1");
        redisTemplate.opsForSet().add("setKey", "setValue");
        redisTemplate.expire("setKey", 60, TimeUnit.SECONDS);
    }

    private void removeSet() {
        Long remove = redisTemplate.opsForSet().remove("setKey", "setValue", "setValue1");
        if (Objects.equals(remove, 2L)) {
            log.info("remove set key successfully.");
        }
    }

}
