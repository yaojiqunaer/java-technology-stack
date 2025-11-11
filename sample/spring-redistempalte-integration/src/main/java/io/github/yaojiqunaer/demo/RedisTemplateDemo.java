package io.github.yaojiqunaer.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

        // 排行
        addSortedSet();
        getSortedSet();

        // 地图
        addGeo();
        getGeo();

        // 分布式锁
        setNx();
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

    private void addSortedSet() {
        redisTemplate.opsForZSet().add("sortedSetKey", "sortedSetValue1", 1);
        redisTemplate.opsForZSet().add("sortedSetKey", "sortedSetValue2", 3);
        redisTemplate.opsForZSet().add("sortedSetKey", "sortedSetValue3", 1.23);
        redisTemplate.opsForZSet().add("sortedSetKey", "sortedSetValue4", 0.34);
        redisTemplate.expire("sortedSetKey", 60, TimeUnit.SECONDS);
    }

    private void getSortedSet() {
        // 1. 获取整个ZSet（升序）
        Set<Serializable> sortedSetKey = redisTemplate.opsForZSet().range("sortedSetKey", 0, -1);
        log.info("升序排列所有成员: {}", sortedSetKey);
        // 2. 获取整个ZSet的属性和分数（升序）
        Set<ZSetOperations.TypedTuple<Serializable>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(
                "sortedSetKey", 0, -1);
        for (ZSetOperations.TypedTuple<Serializable> tuple : typedTuples) {
            log.info("成员: {}, 分数: {}", tuple.getValue(), tuple.getScore());
        }
    }

    private void addGeo() {
        redisTemplate.opsForGeo().add("geo", new Point(116.523453, 20.523453), "user1");
        redisTemplate.opsForGeo().add("geo", new Point(135.523453, 21.523453), "user2");
        redisTemplate.opsForGeo().add("geo", new Point(90.523453, 22.523453), "user3");
        redisTemplate.opsForGeo().add("geo", new Point(178.523453, 40.523453), "user4");
        redisTemplate.expire("geo", 60, TimeUnit.SECONDS);
    }

    private void getGeo() {
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()      // 包含距离信息
                .includeCoordinates()   // 包含坐标信息
                .sortAscending();// 按距离升序排
        GeoResults<RedisGeoCommands.GeoLocation<Serializable>> geo = redisTemplate.opsForGeo().radius("geo",
                new Circle(new Point(100.523453, 25.523453), new Distance(5000.000000, Metrics.KILOMETERS)),
                geoRadiusCommandArgs);
        for (GeoResult<RedisGeoCommands.GeoLocation<Serializable>> result : geo) {
            log.info("成员: {}, 距离: {}", result.getContent().getName(), result.getDistance());
        }
    }

    private void setNx() {
        Boolean nx = redisTemplate.opsForValue().setIfAbsent("Lock", LocalDateTime.now().getNano(), 60,
                TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(nx)) {
            log.info("Thread 1 get lock now");
            Boolean nx2 = redisTemplate.opsForValue().setIfAbsent("Lock", LocalDateTime.now().getNano(), 60,
                    TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(nx2)) {
                log.info("Thread 2 get lock failed");
            }
        }
    }

}
