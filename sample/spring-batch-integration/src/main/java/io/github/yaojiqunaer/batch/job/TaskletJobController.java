package io.github.yaojiqunaer.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Tasklet任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/tasklet")
public class TaskletJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("helloWorldJob")
    private Job helloWorldJob;

    @GetMapping("/start")
    public String startTasklet() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(helloWorldJob, jobParameters);
            return "Tasklet任务已启动";
        } catch (Exception e) {
            log.error("Tasklet任务启动失败", e);
            return "Tasklet任务启动失败: " + e.getMessage();
        }
    }
} 