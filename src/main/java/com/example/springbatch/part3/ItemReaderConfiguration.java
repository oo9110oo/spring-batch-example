package com.example.springbatch.part3;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ItemReaderConfiguration {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

//    @Bean(name = "itemReaderJob")
//    public Job itemReaderJob(JobRepository jobRepository, Step customItemReaderStep, Step csvFileStep, Step jdbcFileStep, Step jpaFileStep) {
//        return new JobBuilder("itemReaderJob", jobRepository)
//                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
//                .start(customItemReaderStep)
//                .next(csvFileStep)
//                .next(jdbcFileStep)
//                .next(jpaFileStep)
//                .build();
//    }
//
//    @Bean
//    public Step customItemReaderStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
//        return new StepBuilder("customItemReaderStep", jobRepository)
//                .<Person, Person>chunk(10, platformTransactionManager)
//                .reader(new CustomItemReader<>(getItems()))
//                .writer(itemWriter())
//                .build();
//    }
//
//    @Bean
//    public Step csvFileStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
//        return new StepBuilder("csvFileStep", jobRepository)
//                .<Person, Person>chunk(10, platformTransactionManager)
//                .reader(csvFileItemReader())
//                .writer(itemWriter())
//                .build();
//    }
//
//    @Bean
//    public Step jdbcFileStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
//        return new StepBuilder("jdbcFileStep", jobRepository)
//                .<Person, Person>chunk(10, platformTransactionManager)
//                .reader(jdbcCursorItemReader())
//                .writer(itemWriter())
//                .build();
//    }
//
//    @Bean
//    public Step jpaFileStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
//        return new StepBuilder("jpaFileStep", jobRepository)
//                .<Person, Person>chunk(10, platformTransactionManager)
//                .reader(jpaCursorItemReader())
//                .writer(itemWriter())
//                .build();
//    }
//
//    private JpaCursorItemReader<Person> jpaCursorItemReader() throws Exception {
//        JpaCursorItemReader<Person> itemReader = new JpaCursorItemReaderBuilder<Person>()
//                .name("jpaCursorItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select p from Person p")
//                .build();
//        itemReader.afterPropertiesSet();
//
//        return itemReader;
//    }
//
//    private JdbcCursorItemReader<Person> jdbcCursorItemReader() throws Exception {
//        JdbcCursorItemReader<Person> itemReader = new JdbcCursorItemReaderBuilder<Person>()
//                .name("jdbcCursorItemReader")
//                .dataSource(dataSource)
//                .sql("select id, name, age, address from person")
//                .rowMapper(((rs, rowNum) -> new Person(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4))))
//                .build();
//        itemReader.afterPropertiesSet();
//        return itemReader;
//    }
//
//
//    private FlatFileItemReader<Person> csvFileItemReader() throws Exception {
//        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
//        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
//        tokenizer.setNames("id", "name", "age", "address");
//        lineMapper.setLineTokenizer(tokenizer);
//
//        lineMapper.setFieldSetMapper(fieldSet -> {
//            int id = fieldSet.readInt("id");
//            String name = fieldSet.readString("name");
//            String age = fieldSet.readString("age");
//            String address = fieldSet.readString("address");
//
//            return new Person(id, name, age, address);
//        });
//
//        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
//                .name("csvFileItemReader")
//                .encoding("UTF-8")
//                .resource(new ClassPathResource("test.csv"))
//                .linesToSkip(1)
//                .lineMapper(lineMapper)
//                .build();
//        itemReader.afterPropertiesSet();
//
//        return itemReader;
//    }
//
//    private ItemWriter<Person> itemWriter() {
//        return items ->
//                items.forEach(item -> {
//                    log.info(item.getName());
//                });
//
////
////                log.info(
////                items.map(Person::getName)
////                .collect(Collectors.joining(",")));
//    }
//
//    private List<Person> getItems() {
//        List<Person> items = new ArrayList<>();
//
//        for (int i =0; i<10; i++) {
//            items.add(new Person(i+1, "test name" + i, "test age", "test address"));
//        }
//
//        return items;
//    }
}
