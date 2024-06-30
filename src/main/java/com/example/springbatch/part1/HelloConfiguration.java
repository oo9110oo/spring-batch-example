package com.example.springbatch.part1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HelloConfiguration {

//    @Bean(name = "heloJob")
//    public Job helloJob(JobRepository jobRepository, Step helloStep) {
//        return new JobBuilder("helloJob", jobRepository)
//                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
//                .start(helloStep)
//                .build();
//    }
//
//    @Bean
//    public Step helloStep(JobRepository jobRepository, @Qualifier("testTaslet") Tasklet testTasklet, PlatformTransactionManager platformTransactionManager) {
//        return new StepBuilder("helloStep", jobRepository)
//                .tasklet(testTasklet, platformTransactionManager).build();
//    }
//
//    @Bean(name="testTaslet")
//    public Tasklet testTaslet() {
//        return (((contribution, chunkContext) -> {
//            log.info("hello spring batch");
//            return RepeatStatus.FINISHED;
//        }));
//    }
}
