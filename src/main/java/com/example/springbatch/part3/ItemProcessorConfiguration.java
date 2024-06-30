package com.example.springbatch.part3;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ItemProcessorConfiguration {

    @Bean(name = "itemProcessorJob")
    public Job itemProcessorJob(JobRepository jobRepository, Step itemProcessorStep) {
        return new JobBuilder("itemProcessorJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
                .start(itemProcessorStep)
                .build();
    }

    @Bean
    public Step itemProcessorStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("itemProcessorStep", jobRepository)
                .<Person, Person>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemWriter<? super Person> itemWriter() {
        return items -> {
            items.forEach(x -> log.info("PERSON.ID : {}", x.getId()));
        };
    }

    private ItemProcessor<? super Person,? extends Person> itemProcessor() {
        return item -> {
            if (item.getId() %2 == 0) {
                return item;
            }

            return null;
        };
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(getItems());
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(new Person(i+1, "test name" + i , "test age", "test address"));
        }

        return items;
    }
}
