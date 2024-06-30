package com.example.springbatch.part3;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ChunkProcessingConfiguration {

//    @Bean(name = "chunkJob")
//    public Job chunkProcessingJob(JobRepository jobRepository, Step chunkStep, Step chunkBaseStep) {
//        return new JobBuilder("chunkJob", jobRepository)
//                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
//                .start(chunkStep)
//                .next(chunkBaseStep)
//                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step chunkBaseStep(JobRepository jobRepository, Tasklet chunkTasklet,
//                              PlatformTransactionManager platformTransactionManager, @Value("#{jobParameters[chunkSize]}") String chunkSize) {
//        return new StepBuilder("chunkStep", jobRepository)
////                .<String, String>chunk(10, platformTransactionManager)      // 첫번째 제네릭 타임은 itemReader에서 읽고 반환된 INPUT 타입, 두번쨰는 OUTPUT 타입
//                .<String, String>chunk(StringUtils.isNotEmpty(chunkSize) ? Integer.parseInt(chunkSize) : 10, platformTransactionManager)
//                .reader(itemReader())
//                .processor(itemProcessor())
//                .writer(itemWriter())
//                .build();
//    }
//
//    private ItemWriter<? super String> itemWriter() {
//        return item -> log.info("chunk item size : {}", item.size());
//        //return items -> items.forEach(log::info);
//    }
//
//    private ItemProcessor<? super String, String> itemProcessor() {
//        // itemProcessor 의 return 값이 null 이면 itemWriter로 값이 넘어가지 않는다
//
//        return item -> item + ", Spring Batch";
//    }
//
//    private ItemReader<String> itemReader() {
//        return new ListItemReader<>(getItems());
//
//    }
//
////    @Bean
////    public Step chunkStep(JobRepository jobRepository, Tasklet chunkTasklet, PlatformTransactionManager platformTransactionManager) {
////        return new StepBuilder("chunkStep", jobRepository)
////                .tasklet(chunkTasklet, platformTransactionManager)
////                .build();
////    }
//
//    @Bean
//    public Step chunkStep(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager platformTransactionManager) {
//        return new StepBuilder("chunkStep", jobRepository)
//                .tasklet(tasklet, platformTransactionManager)
//                .build();
//    }
//
//    @Bean
//    public Tasklet chunkTasklet() {
//        return (((contribution, chunkContext) -> {
//            List<String> items = getItems();
//            log.info("task item size : {}", items.size());
//            return RepeatStatus.FINISHED;
//        }));
//    }
//
////    @Bean
////    public Tasklet tasklet() {
////        List<String> items = getItems();
////
////        return (contribution, chunkContext) -> {
////            StepExecution stepExecution = contribution.getStepExecution();
////            JobParameters jobParameters = stepExecution.getJobParameters();
////
////            String value = jobParameters.getString("chunkSize", "10");
////            int chunkSize = StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : 10;
////            int fromIndex = (int) stepExecution.getReadCount();
////            int toIndex = fromIndex + chunkSize;
////
////            if (fromIndex >= items.size()) {
////                return RepeatStatus.FINISHED;
////            }
////
////            List<String> subList = items.subList(fromIndex, toIndex);
////
////            log.info("task item size : {}", subList.size());
////
////            stepExecution.setReadCount(toIndex);
////
////            return RepeatStatus.CONTINUABLE;
////        };
////    }
//
//    @Bean
//    @StepScope
//    public Tasklet tasklet(@Value("#{jobParameters[chunkSize]}") String value) {
//        List<String> items = getItems();
//
//        return (contribution, chunkContext) -> {
//            StepExecution stepExecution = contribution.getStepExecution();
////            JobParameters jobParameters = stepExecution.getJobParameters();
////
////            String value = jobParameters.getString("chunkSize", "10");
//            int chunkSize = StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : 10;
//            int fromIndex = (int) stepExecution.getReadCount();
//            int toIndex = fromIndex + chunkSize;
//
//            if (fromIndex >= items.size()) {
//                return RepeatStatus.FINISHED;
//            }
//
//            List<String> subList = items.subList(fromIndex, toIndex);
//
//            log.info("task item size : {}", subList.size());
//
//            stepExecution.setReadCount(toIndex);
//
//            return RepeatStatus.CONTINUABLE;
//        };
//    }
//
//    private List<String> getItems() {
//        List<String> items = new ArrayList<>();
//        for (int i =0; i< 100; i++) {
//            items.add(i + " Hello");
//        }
//        return items;
//    }
}
