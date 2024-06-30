package com.example.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
//        SpringApplication.run(SpringBatchApplication.class, args);
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchApplication.class, args)));

    }

    @Bean
    @Primary
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setThreadNamePrefix("batch-thread-");
        taskExecutor.initialize();

        return taskExecutor;
    }

}
