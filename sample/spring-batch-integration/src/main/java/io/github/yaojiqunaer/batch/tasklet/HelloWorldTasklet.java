package io.github.yaojiqunaer.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 简单的Hello World Tasklet示例
 */
@Slf4j
@Component
public class HelloWorldTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("Hello World from Spring Batch Tasklet!");
        return RepeatStatus.FINISHED;
    }
} 