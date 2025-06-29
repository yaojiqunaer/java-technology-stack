package io.github.yaojiqunaer.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

/**
 * Spring Batch 集成示例应用
 */
@SpringBootApplication
@EnableBatchProcessing
public class BatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
} 