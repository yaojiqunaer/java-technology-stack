package io.github.yaojiqunaer.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 批处理任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/batch")
public class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importPersonJob;

    @GetMapping("/start")
    public String startBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(importPersonJob, jobParameters);
            return "批处理任务已启动";
        } catch (Exception e) {
            log.error("批处理任务启动失败", e);
            return "批处理任务启动失败: " + e.getMessage();
        }
    }
} 