package io.github.yaojiqunaer.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/03/29 22:49
 * @Author xiaodongzhang
 */
@Service
@Slf4j
public class CacheService {

    public record User(String name, Integer age) implements Serializable {
    }

    static Map<String, Serializable> DATABASES = new HashMap<>();

    static {
        DATABASES.put("1", new User("yaojiqunaer", 18));
        DATABASES.put("2", new User("lishi", 18));
        DATABASES.put("3", new User("wangwu", 18));
    }

    @Cacheable(value = "USERS", key = "#a0", unless = "#result == null")
    public Serializable getUser(String key) {
        log.info("query from database, key: {}", key);
        return DATABASES.get(key);
    }

    @CachePut(value = "USERS", key = "#a0")
    public Serializable updateUser(String key, Serializable value) {
        log.info("update database, key: {}, value: {}", key, value);
        DATABASES.put(key, value);
        return value;
    }

    @CacheEvict(value = "USERS", key = "#a0")
    public Serializable deleteUser(String key) {
        log.info("delete database, key: {}", key);
        return DATABASES.remove(key);
    }

    @Cacheable(value = "USERS", key = "#a0")
    public User cache(String key) {
        throw new NotExistException();
    }

    public class NotExistException extends RuntimeException {
        public NotExistException() {
            super();
        }
    }

}
