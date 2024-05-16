package io.github.yaojiqunaer.distributedlock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * 用于标注在方法上，以实现分布式锁的功能。
 * 提供了丰富的配置选项，以适应不同场景下的需求。
 *
 * @Create Date: 2024/05/13 17:32
 * @Author xiaodongzhang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DistributedLock {

    /**
     * 锁的键。
     * 用于标识唯一锁。
     *
     * @return 锁的键值。
     */
    String key();

    /**
     * 等待获取锁的超时时间。
     *
     * @return 等待获取锁的超时时间值。
     */
    long waitTime() default 5000;

    /**
     * 锁的持有时间。
     *
     * @return 锁的持有时间值。
     */
    long leaseTime() default 5000;

    /**
     * 时间单位。
     *
     * @return 时间单位枚举。
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 是否自动释放锁。
     *
     * @return 是否自动释放锁的布尔值。
     */
    boolean autoRelease() default true;

    /**
     * 是否启用公平锁机制。
     *
     * @return 公平锁启用状态。
     */
    boolean fair() default false;

    /**
     * 自动续期间隔时间，-1表示不续期。
     *
     * @return 续期间隔时间。
     */
    long renewInterval() default -1;

    /**
     * 获取锁失败时的重试次数。
     *
     * @return 重试次数。
     */
    int retryAttempts() default 3;

    /**
     * 锁超时处理策略。
     *
     * @return 超时处理策略。
     */
    TimeoutStrategy timeoutStrategy() default TimeoutStrategy.THROW_EXCEPTION;

    /**
     * 异常处理器类。
     *
     * @return 异常处理器类。
     */
    Class<? extends ExceptionHandler> exceptionHandler() default DefaultExceptionHandler.class;

    enum TimeoutStrategy {
        THROW_EXCEPTION,
        RETURN_NULL,
        CUSTOM
        // ... 其他策略
    }

    interface ExceptionHandler {
        void handle(Exception e);
    }

    class DefaultExceptionHandler implements ExceptionHandler {
        @Override
        public void handle(Exception e) {
            throw new RuntimeException("Failed to acquire lock", e);
        }
    }
}
