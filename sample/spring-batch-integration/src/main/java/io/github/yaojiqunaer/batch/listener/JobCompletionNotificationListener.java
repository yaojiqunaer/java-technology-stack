package io.github.yaojiqunaer.batch.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 任务完成通知监听器
 */
@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job is starting: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job parameters: {}", jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job completed successfully: {}", jobExecution.getJobInstance().getJobName());
        } else {
            log.error("Job failed: {}", jobExecution.getJobInstance().getJobName());
            log.error("Exit status: {}", jobExecution.getExitStatus().getExitCode());
            log.error("Exit description: {}", jobExecution.getExitStatus().getExitDescription());
        }
    }
} 