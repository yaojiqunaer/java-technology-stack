package io.github.yaojiqunaer.controller;

import io.github.yaojiqunaer.cache.CacheService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Random;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/03/29 23:53
 * @Author xiaodongzhang
 */
@RestController
@AllArgsConstructor
@Slf4j
public class RedisController {

    private RedisTemplate<String, Serializable> redisTemplate;

    private CacheManager cacheManager;

    private CacheService cacheService;

    @PostConstruct
    public void init() {
    }

    @GetMapping("/redis/{type}/{id}")
    public Serializable get(@PathVariable("type") String type, @PathVariable("id") String id) {
        return switch (type) {
            case "get":
                yield cacheService.getUser(id);
            case "put":
                yield cacheService.updateUser(id, new CacheService.User(id, new Random().nextInt()));
            case "delete":
                yield cacheService.deleteUser(id);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    @GetMapping({"/cache/remove/{cacheName}/{cacheKey}", "/cache/remove/{cacheName}"})
    public Serializable remove(@PathVariable String cacheName, @PathVariable(required = false) String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cacheKey == null) {
            cache.clear();
        } else {
            cache.evict(cacheKey);
        }
        return null;
    }

    @GetMapping("/cache/lock/{key}")
    public Serializable lock(@PathVariable String key) {
        return exist(key);
    }

    public boolean exist(String key) {
        try {
            cacheService.cache(key);
        } catch (CacheService.NotExistException e) {
            return false;
        }
        return true;
    }

}
