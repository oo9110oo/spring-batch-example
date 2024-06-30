package com.example.springbatch.part3;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SavePersonConfiguration {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job savePersonJob(JobRepository jobRepository, Step savePersonStep) {
        return new JobBuilder("savePersonJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
                .start(savePersonStep)
                .listener(new SavePersonListener.SavePersonJobExecutionListener())
                .listener(new SavePersonListener.SavePersonAnnotationJobExecution())
                .build();
    }

    @Bean
    @JobScope
    public Step savePersonStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
                               @Value("#{jobParameters[allow_duplicate]}") String allowDuplicate
                               ) throws Exception {
        return new StepBuilder("savePersonStep", jobRepository)
                .<Person, Person>chunk(10,platformTransactionManager)
                .reader(itemReader())
                //.processor(new DuplicationValidationProcessor<>(Person::getName, Boolean.getBoolean(allowDuplicate)))
                .processor(itemProcessor(allowDuplicate))
                .writer(itemWriter())
                .listener(new SavePersonListener.SavePersonStepExecutionListener())
                .faultTolerant()
                .skip(NotFoundNameException.class)
                .skipLimit(2)
                .build();
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor(String allowDuplicate) throws Exception {
        DuplicationValidationProcessor<Person> duplicationValidationProcessor =
                new DuplicationValidationProcessor<Person>(Person::getName, Boolean.getBoolean(allowDuplicate));

        ItemProcessor<Person, Person> validationProcessor = item -> {
            if (item.isNotEmptyName()) {
                return item;
            }

            throw new NotFoundNameException();
        };

        CompositeItemProcessor<Person, Person> itemProcessor = new CompositeItemProcessorBuilder()
                .delegates(new PersonValidationRetryProcessor(), validationProcessor, duplicationValidationProcessor)
                .build();

        itemProcessor.afterPropertiesSet();

        return itemProcessor;

    }

    private ItemWriter<Person> itemWriter() throws Exception {
        //return items -> items.forEach(x -> log.info("저는 {} 입니다.", x.getName()));
        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .build();

        ItemWriter<Person> logItemWriter = items -> log.info("person.size : {}", items.size());

        CompositeItemWriter<Person> itemWriter = new CompositeItemWriterBuilder<Person>()
                .delegates(jpaItemWriter, logItemWriter)
                .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;

    }

    private ItemReader<Person> itemReader() throws Exception {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("name", "age", "address");
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            String name = fieldSet.readString(0);
            String age = fieldSet.readString(1);
            String address = fieldSet.readString(2);
            return new Person(name, age, address);
        });

        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
                .name("savePersonItemReader")
                .encoding("UTF-8")
                .linesToSkip(1)
                .resource(new ClassPathResource("person.csv"))
                .lineMapper(lineMapper)
                .build();

        itemReader.afterPropertiesSet();

        return itemReader;
    }
}
