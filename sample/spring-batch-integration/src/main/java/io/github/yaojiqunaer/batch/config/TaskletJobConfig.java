package io.github.yaojiqunaer.batch.config;

import io.github.yaojiqunaer.batch.listener.JobCompletionNotificationListener;
import io.github.yaojiqunaer.batch.tasklet.HelloWorldTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 使用Tasklet的任务配置类
 */
@Configuration
public class TaskletJobConfig {

    @Autowired
    private HelloWorldTasklet helloWorldTasklet;

    @Autowired
    private JobCompletionNotificationListener listener;

    @Bean
    public Job helloWorldJob(JobRepository jobRepository, @Qualifier("helloWorldStep") Step helloWorldStep) {
        return new JobBuilder("helloWorldJob", jobRepository)
                .listener(listener)
                .start(helloWorldStep)
                .build();
    }

    @Bean
    public Step helloWorldStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloWorldStep", jobRepository)
                .tasklet(helloWorldTasklet, transactionManager)
                .build();
    }
} 