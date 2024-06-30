package com.example.springbatch.part2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class SharedConfiguration {

//    @Bean(name = "shareJob")
//    public Job shareJob(JobRepository jobRepository, Step shareStep, Step shareStep2) {
//        return new JobBuilder("shardJob", jobRepository)
//                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
//                .start(shareStep)
//                .next(shareStep2)
//                .build();
//    }
//
//    @Bean
//    public Step shareStep(JobRepository jobRepository, Tasklet shareTasklet, PlatformTransactionManager platformTransactionManager) {
//        return new StepBuilder("shareStep", jobRepository)
//                .tasklet(shareTasklet, platformTransactionManager).build();
//    }
//
//    @Bean
//    public Tasklet shareTasklet() {
//        return (((contribution, chunkContext) -> {
//            StepExecution stepExecution = contribution.getStepExecution();
//            ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
//            stepExecutionContext.putString("stepKey", "step execution context");
//
//            JobExecution jobExecution = stepExecution.getJobExecution();
//            JobInstance jobInstance = jobExecution.getJobInstance();
//            ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
//            JobParameters jobParameters = jobExecution.getJobParameters();
//
//            log.info("jobName : {}, stepName : {}, parameter : {}",
//                    jobInstance.getJobName(),
//                    stepExecution.getStepName(),
//                    jobParameters.getLong("run.id"));
//                    //jobParameters.getParameters().get("run.id"));
//
//            return RepeatStatus.FINISHED;
//        }));
//    }
//
//    @Bean
//    public Step shareStep2(JobRepository jobRepository, Tasklet shareTasklet2, PlatformTransactionManager platformTransactionManager) {
//        return new StepBuilder("shareStep2", jobRepository)
//                .tasklet(shareTasklet2, platformTransactionManager).build();
//    }
//
//    @Bean
//    public Tasklet shareTasklet2() {
//        return (((contribution, chunkContext) -> {
//            StepExecution stepExecution = contribution.getStepExecution();
//            ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
//
//            JobExecution jobExecution = stepExecution.getJobExecution();
//            ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
//
//            log.info("jobKey : {}, stepKey : {}",
//                    jobExecutionContext.getString("jobKey", "emptyJobKey"),
//                    stepExecutionContext.getString("stepKey", "emptyStepKey"));
//
//            return RepeatStatus.FINISHED;
//        }));
//    }

}
