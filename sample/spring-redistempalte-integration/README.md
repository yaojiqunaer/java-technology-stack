# Redis

[TOC]



## Redis数据类型

### String

#### 常用命令

可以借助https://try.redis.io/网站模拟在线的Redis控制台

```shell
# 设置 key-value 类型的值
> set key value
# 获取 key 对应的 value
> get key
# 是否存在 key
> exists key
# 获取 key 对应 value 的长度
> strlen key
# 删除 key
> del key
# 批量设置 key-value 类型的值
> mset key1 value1 key2 value2 
# 批量获取多个 key 对应的 value
> mget key1 key2 
# number 的 value 增加 1
> incr number
# number 的 value 增加 10
> incrby number 10
# number 的 value 减少 1
> decr number
# number 的 value 减少 10
> decrby number 10
# 设置 key 60秒过期（key已存在）
> expire key 60 
# 查看 key 过期时长（秒）
> ttl key
# 设置key-value, 过期时长为60秒
> SET key value EX 60
> SETEX key 60 value
# 不存在才增加一个key(原子操作)
> SETNX key value
```

#### 使用场景

##### 缓存对象

如缓存登录用户的信息，通常将这些信息转成JSON字符串存储，如

key: `user_1`

value: `'{"name":"zhangshan", "age":18}'`

##### 计数

利用Redis的命令`incr`、`incrby`、`decr`、`decrby`，实现对数字的增减，常用于计算访问次数、点赞、转发、库存数量等场景。当然这些数据仅仅作缓存使用，实际还是需要持久化存储到DB（需保证DB和缓存的一致性）

##### 分布式锁

可以借助Redis分布式锁来控制并发访问。如，集群的各个节点减库存的操作，需要保证所有节点同一时刻只有一个节点对库存量进行操作，此时如果X节点持有锁，那么其它节点都需要阻塞。

命令参考：`set distributedlock uuid nx px 100000`，该命令是一个原子操作，如果key不存在，则set一个ttl为10s的key，key存在时则set失败。

- distributedlock - 分布式锁key
- uuid - value, 可以任意设置
- nx - not exits, 不存在才会成功
- px - 过期时间，防止锁永远无法释放
- 100000 - 过期时间（毫秒），需根据业务决定，建议比业务处理完的时间长（如果快过期都没有处理完业务，需要对锁进行延期）

##### Session会话

​	后端系统通常会保存用户的登录状态/信息，但是如果是分布式系统，同一个用户访问不同节点就会出现问题，此时，可以在用户登录时将Session的信息保存到Redis，后续访问不同节点只需要去获取Redis中的Session即可。

​	**基本思路**：

1. 用户登录，登录成功
2. 将sessionId（token/uuid等）和user的信息存到Redis，如 key = `sso:user:{{token}}, value=`'{"id":"1", "name":"zhangshan", "age":18}'`
3. 将sessionId设置过期时间（登录有效期）
4. 用户访问系统（商品、订单、支付系统）
5. 每个系统都用sessionId去Redis获取用户信息，如果不存在代表未登录或登录过期

### List

### Hash

### Set

### ZSet

### BitMap

### HyperLogLog

### GEO

### Stream



https://xiaolincoding.com/redis/



## Spring Data Redis SetUp

### 添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>3.2.5</version>
</dependency>
```

### 配置连接信息

<!--SpringBoot 3.x，redis的配置默认使用spring.data.redis作为前缀，2.x使用的则是spring.redis-->

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      # 连接超时时间（记得添加单位，Duration）
      timeout: 10000ms
      # Redis默认情况下有16个分片，这里配置具体使用的分片
      # database: 0
      lettuce:
        pool:
          # 连接池最大连接数（使用负值表示没有限制） 默认 8
          max-active: 8
          # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
          max-wait: -1ms
          # 连接池中的最大空闲连接 默认 8
          max-idle: 8
          # 连接池中的最小空闲连接 默认 0
          min-idle: 0
```

### 注入配置RedisConfig

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        // 默认应该是<String,String>, 这里扩展为支持所有序列化
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
```

### 使用RedisTemplate

```java
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

    @PostConstruct
    public void init() {
				redisTemplate.opsForValue().set("key", "value", 60, TimeUnit.SECONDS);
    }

}
```

